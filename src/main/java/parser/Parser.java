package parser;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface Parser {

    Map<String, Set<String>> parse() throws InterruptedException, ExecutionException;
}
