package cfda.process.integration;

import cfda.delegates.PostTweetDelegate;
import cfda.util.MockEl;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

@MockEl
public class Mocks {

    @Mock
    @Named
    @Produces
    public PostTweetDelegate postTweetDelegate;


    public Mocks(){
        MockitoAnnotations.initMocks(this);
    }

}
