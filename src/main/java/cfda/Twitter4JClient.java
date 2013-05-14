package cfda;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter4JClient {
    public ResponseList<Location> availTrends() {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("c48hAsrDQxfAhq0Ao7nOQ")
                    .setOAuthConsumerSecret("6VJmoJnmqct8hzNW4NtN1KTrAgeew7CUEskGSiX3nA")
                    .setOAuthAccessToken("14617010-37XXygdFNC5jUK4HDhkfNIOuBRrQnNAQdutS8fFpo")
                    .setOAuthAccessTokenSecret("obhVbfAlGqeOZky18601sIy2CsW10JXqdyN66dRFQ");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            ResponseList<Location> locations;
            locations = twitter.getAvailableTrends();
            System.out.println("Showing available trends");
            Location hamburg = Collections2.filter(locations, new Predicate<Location>() {
                @Override
                public boolean apply( Location location) {
                    return location.getName().equals("Hamburg");
                }
            }).iterator().next();

            for (Location location : locations) {
                //    System.out.println(location.getName() + " (woeid:" + location.getWoeid() + ")");
            }
            Trends placeTrends = twitter.getPlaceTrends(hamburg.getWoeid());
            Trend[] trends = placeTrends.getTrends();
            for (int i = 0; i < trends.length; i++) {
                Trend trend = trends[i];
                System.out.printf("\nname: %s query: %s url: %s", trend.getName(), trend.getQuery(), trend.getURL());
            }
            System.out.println("done.");
            return locations;
        } catch (TwitterException te) {
            throw new RuntimeException(te);
        }
    }
}
