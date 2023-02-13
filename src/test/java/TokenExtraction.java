import org.testng.Assert;
import org.testng.annotations.Test;

public class TokenExtraction {
    @Test
    public void testGetBearerToken() {
        Token token = Token.getInstance();
        String writeToken = token.getBearerToken("write");
        String readToken = token.getBearerToken("read");

        Assert.assertFalse(writeToken.isEmpty());
        Assert.assertFalse(readToken.isEmpty());
    }
}
