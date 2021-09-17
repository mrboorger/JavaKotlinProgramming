import java.util.ArrayList;
import java.util.List;

public class ParserImpl implements Parser {
    public Expression parseExpression(String input) throws ExpressionParseException {
        // divide input to tokens
        for (var strTok : input.split("\\s+")) {
            switch (strTok) {
                case "-": {
                    
                }
                case "+": {

                }
                default: {
                    // literal
                }
            }
        }
        return null;
    }
}
