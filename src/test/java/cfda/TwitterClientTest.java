package cfda;

import org.junit.Test;

public class TwitterClientTest {
    @Test
    public void testTrends() throws Exception {
        TwitterClient twitterClient = new TwitterClient();
        twitterClient.trends();

        Twitter4JClient twitter4JClient = new Twitter4JClient();
        twitter4JClient.availTrends();
    }

    @Test
    public void testUserInfo() throws Exception {
        TwitterClient twitterClient = new TwitterClient();
        String userInfoOnSistar = twitterClient.userInfo("sistar");
        System.out.printf("user Info %s", userInfoOnSistar);
    }
}
