package me.bottdev.kern.dependency.graph;

import me.bottdev.kern.dependency.DependOrder;
import me.bottdev.kern.dependency.DependencyLink;
import me.bottdev.kern.dependency.Task;
import me.bottdev.kern.dependency.exceptions.ResolverForgetException;
import me.bottdev.kern.struct.graph.EndpointPairs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static me.bottdev.kern.dependency.Task.task;
import static org.junit.jupiter.api.Assertions.*;

class GraphDependencyResolverStateTest {

    private GraphDependencyResolverState<String, Task> state;

    @BeforeEach
    void setUp() {
        state = new GraphDependencyResolverState<>();
    }


    @Nested
    class Forget {

        static Stream<Arguments> forgetArguments() {
            return Stream.of(
                    Arguments.of("1", true),
                    Arguments.of("2", true),
                    Arguments.of("3", true),
                    Arguments.of("4", false)
            );
        }

        @ParameterizedTest
        @MethodSource("forgetArguments")
        void forget(String id, boolean shouldThrow) {

            state.commit(task("1").build());
            state.commit(task("2").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build());
            state.commit(task("3").dependsOn("1", DependencyLink.REQUIRED, DependOrder.AFTER).build());
            state.commit(
                    task("4")
                            .dependsOn("2", DependencyLink.REQUIRED, DependOrder.AFTER)
                            .dependsOn("3", DependencyLink.REQUIRED, DependOrder.AFTER)
                            .build()
            );

            state.graph().addEdge(EndpointPairs.directed("2", "1"));
            state.graph().addEdge(EndpointPairs.directed("3", "1"));
            state.graph().addEdge(EndpointPairs.directed("4", "2"));
            state.graph().addEdge(EndpointPairs.directed("4", "3"));

            if (shouldThrow) {
                assertThrows(ResolverForgetException.class, () -> state.forget(id));

            } else {
                boolean before = state.remembers(id);
                state.forget(id);

                assertTrue(before);
                assertFalse(state.remembers(id));

            }

        }

    }

}