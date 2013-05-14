package cfda;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;

import javax.ws.rs.core.UriBuilder;

public class TwitterClient {


    ClientRequestFactory crf = new ClientRequestFactory(UriBuilder.fromUri(
            "http://api.twitter.com/1").build());
    ClientRequest trendsRequest = crf
            .createRelativeRequest("/trends/1.json");

    public void trends() throws Exception {
        String trends = trendsRequest.get(String.class).getEntity();
        System.out.println(trends);
    }
    public String userInfo(String user) throws Exception {
        // The information of some Twitter's accounts
        ClientRequest userInfoRequest = crf
                .createRelativeRequest("/users/show.json");
        userInfoRequest.queryParameter("screen_name", user);
        String entity = userInfoRequest.get(String.class).getEntity();
        userInfoRequest.clear();
        return entity;
    }
}
