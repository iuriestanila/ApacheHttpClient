import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class TokenExtraction {
    public static Logger log = LogManager.getLogger();
    @Test
    public void testGetBearerToken() {
        Token token = Token.getInstance();
        String writeToken = token.getBearerToken("write");
        String readToken = token.getBearerToken("read");

        log.info("\n writeToken: " + writeToken +
                "\n readToken: " + readToken);

    }
}
