import com.Config;
import com.PubSub;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TC_PubSub {

    PubSub pubsub = new PubSub();
    Config config = new Config();
    String projectId = "local-pubsub";
    String topicId = "my-topic-5";

    @Test
    public void TestCreateTopic() throws IOException, ExecutionException, InterruptedException {
        pubsub.createTopicExample(projectId, topicId, config);
        pubsub.publisherExample(projectId, topicId, config);
    }
}
