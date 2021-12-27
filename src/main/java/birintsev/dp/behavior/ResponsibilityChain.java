package birintsev.dp.behavior;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import static java.lang.Double.parseDouble;

public class ResponsibilityChain {

    public static void main(String[] args) {
        Iterable<RequestHandler> handlers = Arrays.asList(
            new StringContainerRequestHandler(),
            new EvenNumberRequestHandler(),
            new StringContainerRequestHandler()
        );

        Iterable<Request> requests = Arrays.asList(
            new StringContainerRequest("request1"),
            new StringContainerRequest("12"),
            new StringContainerRequest("13")
        );

        requests.forEach(
            request -> {
                for (RequestHandler requestHandler : handlers) {
                    if (requestHandler.canHandle(request)
                        && requestHandler.handle(request)
                    ) {
                        break;
                    }
                }
            }
        );
    }

    public interface RequestHandler {

        boolean canHandle(Request request);

        /**
         * @return {@code true} if the request has been handled
         *         and should <strong>not</strong> be passed further
         *         through the handlers chain.
         *
         * @exception IllegalArgumentException
         *            if the {@code request} can not be handled
         *            by this handler.
         * */
        boolean handle(Request request);
    }

    public interface Request {

    }

    @RequiredArgsConstructor
    @Getter
    public static class StringContainerRequest implements Request {
        private final String string;
    }

    public static class StringContainerRequestHandler implements RequestHandler {

        @Override
        public boolean canHandle(Request request) {
            return request instanceof StringContainerRequest;
        }

        @Override
        public boolean handle(Request request) {
            try {
                System.out.printf(
                    "%s: %s%s",
                        this.getClass().getSimpleName(),
                        ((StringContainerRequest) request).getString(),
                        System.lineSeparator()
                );
                return false;
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(
                    String.format(
                        "The request is not a %s", StringContainerRequest.class
                    ),
                    e
                );
            }
        }
    }

    public static class NumberStringRequestHandler
    extends StringContainerRequestHandler {

        @Override
        public boolean canHandle(Request request) {
            if (!super.canHandle(request)) {
                return false;
            }

            try {
                StringContainerRequest stringContainerRequest =
                    (StringContainerRequest) request;
                parseDouble(stringContainerRequest.getString());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public static class EvenNumberRequestHandler
    extends NumberStringRequestHandler {

        @Override
        public boolean canHandle(Request request) {
            return super.canHandle(request)
                && parseDouble(((StringContainerRequest) request).getString())
                    % 2 == 0;
        }

        @Override
        public boolean handle(Request request) {
            super.handle(request);
            return true;
        }
    }
}
