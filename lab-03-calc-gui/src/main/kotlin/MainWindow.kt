import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.lang.Integer.min
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class MainWindow : JFrame {
    private var expression: Expression? = null
    private val inputField: JTextField
    private val resultTextField: JTextField
    private val l1 = DefaultListModel<String>()
    private val buttonNames: Array<String> = arrayOf(
        "1", "2", "3", "+",
        "4", "5", "6", "-",
        "7", "8", "9", "*",
        "(", "0", ")", "/"
    )
    private val variables: HashMap<String, Double> = HashMap()
    private val parser = ParserImpl()

    constructor() : super() {
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.setSize(500, 400)

        val mainLayout = GridBagLayout()
        val mainLayoutPanel = JPanel()
        mainLayoutPanel.layout = mainLayout
        this.contentPane.add(mainLayoutPanel)

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.BOTH
        constraints.gridy = 0
        constraints.gridx = 0
        constraints.weightx = 1.0
        constraints.weighty = 0.2

        inputField = createInputField()
        mainLayoutPanel.add(inputField, constraints)

        resultTextField = createResultTextField()
        constraints.gridy = 1
        constraints.weightx = 0.2
        mainLayoutPanel.add(resultTextField, constraints)

        constraints.gridy = 2
        constraints.weighty = 0.6
        mainLayoutPanel.add(createKeyboard(), constraints)

        constraints.gridy = 3
        constraints.weighty = 0.07
        mainLayoutPanel.add(createVisitorsButton(), constraints)

        constraints.gridy = 4
        constraints.weighty = 0.07
        mainLayoutPanel.add(createDelButton(), constraints)

        val varButton = JButton("Add variable")
        varButton.addActionListener { addVariable() }
        constraints.gridy = 5
        constraints.weighty = 0.07
        mainLayoutPanel.add(varButton, constraints)

        constraints.gridy = 6
        constraints.weighty = 0.3
        mainLayoutPanel.add(creatVarList(), constraints)
    }

    private fun showMessage(message: String) {
        JOptionPane.showMessageDialog(this, message)
    }

    private fun rebuildVarList() {
        l1.clear()
        for ((key, value) in variables) {
            l1.add(0, "$key = $value")
        }
        recalcExpression()
    }

    private fun addVariable() {
        val result =
            JOptionPane.showInputDialog(
                "<html><h2>Введите перменную (var = xxxx.xxx)." +
                        " \nДля удаления используйте двойной клик"
            ).filter { !it.isWhitespace() }
                .split("=")
        if (result.size == 2) {
            if (!ParserImpl.isVariable(result[0])) {
                showMessage("<html><h2>Неверный формат")
                return
            }
            try {
                variables[result[0]] = result[1].toDouble()
                rebuildVarList()
            } catch (e: NumberFormatException) {
                showMessage("<html><h2>Неверный формат")
                return
            }
        } else {
            showMessage("<html><h2>Неверный формат")
            return
        }
    }

    private fun recalcExpression() {
        try {
            expression = parser.parseExpression(inputField.text)
            expression?.accept(SetVariablesVisitor(variables))
            resultTextField.text = expression?.accept(ComputeExpressionVisitor.INSTANCE).toString()
        } catch (e: ExpressionParseException) {
            expression = null
            resultTextField.text = "Wrong expression"
        }
    }

    private fun createDelButton(): JButton {
        val delButton = JButton("Del")
        delButton.addActionListener {
            if (inputField.text.isNotEmpty()) {
                inputField.text = inputField.text.substring(0, inputField.text.length - 1)
            }
        }
        return delButton
    }

    private fun createVisitorsButton(): JButton {
        val visitorsButton = JButton("Visitors")
        visitorsButton.addActionListener {
            showMessage(
                "Depth: " + expression?.accept(CalcDepthVisitor.INSTANCE).toString() + "\n" +
                        "Debug: " + expression?.accept(DebugRepresentationExpressionVisitor.INSTANCE).toString()
            )
        }
        return visitorsButton
    }

    private fun creatVarList(): JList<String> {
        val list = JList(l1)
        list.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(me: MouseEvent) {
                if (me.clickCount == 2) {
                    val value: String = list.selectedValue
                    variables.remove(value.split("=")[0].filter { !it.isWhitespace() })
                    l1.remove(list.selectedIndex)
                    recalcExpression()
                }
            }
        })
        return list
    }

    private fun createInputField(): JTextField {
        val field = JTextField()
        addResizeListener(field)

        field.document.addDocumentListener(object : DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                recalcExpression()
            }

            override fun removeUpdate(e: DocumentEvent) {
                recalcExpression()
            }

            override fun insertUpdate(e: DocumentEvent) {
                recalcExpression()
            }
        })
        return field
    }

    private fun createResultTextField(): JTextField {
        val field = JTextField()
        addResizeListener(field)
        field.isEditable = false
        return field
    }

    private fun createKeyboard(): JPanel {
        val keyboardLayout = GridLayout(4, 3, 6, 6)
        val keyboardPanel = JPanel(keyboardLayout)

        for (name in buttonNames) {
            val button = JButton(name)
            val str = name

            button.addActionListener { inputField.text = inputField.text + str }
            addResizeListener(button)
            keyboardPanel.add(button)
        }
        return keyboardPanel
    }

    private fun addResizeListener(comp: JComponent) {
        comp.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                comp.font = Font("TimesRoman", Font.BOLD, min(comp.width, comp.height) / 2)
            }
        })
    }
}
