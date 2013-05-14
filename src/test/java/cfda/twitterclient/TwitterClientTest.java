package cfda.twitterclient;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;

public class TwitterClientTest {
    @Test
    public void testTwitterClient() throws Exception {

        // A factory for the Twitter REST API
        ClientRequestFactory crf = new ClientRequestFactory(UriBuilder.fromUri(
                "http://api.twitter.com/1").build());

        // What is trending?
        ClientRequest trendsRequest = crf
                .createRelativeRequest("/trends/1.json");
        String trends = trendsRequest.get(String.class).getEntity();
        System.out.println(trends);

        // The information of some Twitter's accounts
        ClientRequest userInfoRequest = crf
                .createRelativeRequest("/users/show.json");


        userInfoRequest.queryParameter("screen_name", "sistar");

        System.out.println(userInfoRequest.get(String.class).getEntity());
        userInfoRequest.clear();
    }
}