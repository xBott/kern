package me.bottdev.kern.commons.registry.types;

import me.bottdev.kern.commons.key.SimpleTypedKey;
import me.bottdev.kern.commons.key.TypedKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SimpleTypedKeyRegistryTest {

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    private SimpleTypedKeyRegistry<String> registry;

    private final TypedKey<String> KEY_A   = SimpleTypedKey.of(String.class, "alpha");
    private final TypedKey<String> KEY_B   = SimpleTypedKey.of(String.class, "beta");
    private final TypedKey<String> KEY_DEF = SimpleTypedKey.of(String.class, "");

    @BeforeEach
    void setUp() {
        registry = new SimpleTypedKeyRegistry<>();
    }

    // =========================================================================
    // register
    // =========================================================================
    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("registers a value and makes it retrievable")
        void should_RegisterValue_When_KeyIsNew() {
            // when
            registry.register(KEY_A, "valueA");

            // then
            assertThat(registry.get(KEY_A)).isEqualTo("valueA");
        }

        @Test
        @DisplayName("throws when key is already occupied")
        void should_ThrowIllegalState_When_KeyAlreadyRegistered() {
            // given
            registry.register(KEY_A, "first");

            // when / then
            assertThatThrownBy(() -> registry.register(KEY_A, "second"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already registered");
        }

        @Test
        @DisplayName("throws on null key")
        void should_ThrowNPE_When_KeyIsNull() {
            assertThatThrownBy(() -> registry.register(null, "v"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("throws on null value")
        void should_ThrowNPE_When_ValueIsNull() {
            assertThatThrownBy(() -> registry.register(KEY_A, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // =========================================================================
    // registerIfAbsent
    // =========================================================================
    @Nested
    @DisplayName("registerIfAbsent()")
    class RegisterIfAbsent {

        @Test
        @DisplayName("invokes factory and stores result when key is absent")
        void should_InvokeFactory_When_KeyAbsent() {
            // when
            registry.registerIfAbsent(KEY_A, k -> "computed");

            // then
            assertThat(registry.get(KEY_A)).isEqualTo("computed");
        }

        @Test
        @DisplayName("does not overwrite an existing value")
        void should_NotOverwrite_When_KeyPresent() {
            // given
            registry.register(KEY_A, "original");

            // when
            registry.registerIfAbsent(KEY_A, k -> "should-not-appear");

            // then
            assertThat(registry.get(KEY_A)).isEqualTo("original");
        }
    }

    // =========================================================================
    // registerOrReplace
    // =========================================================================
    @Nested
    @DisplayName("registerOrReplace()")
    class RegisterOrReplace {

        @Test
        @DisplayName("inserts when absent")
        void should_Insert_When_KeyAbsent() {
            registry.registerOrReplace(KEY_A, "new");
            assertThat(registry.get(KEY_A)).isEqualTo("new");
        }

        @Test
        @DisplayName("replaces an existing value silently")
        void should_Replace_When_KeyPresent() {
            // given
            registry.register(KEY_A, "old");

            // when
            registry.registerOrReplace(KEY_A, "new");

            // then
            assertThat(registry.get(KEY_A)).isEqualTo("new");
        }
    }

    // =========================================================================
    // isRegistered
    // =========================================================================
    @Nested
    @DisplayName("isRegistered()")
    class IsRegistered {

        @Test
        @DisplayName("returns true after registration")
        void should_ReturnTrue_When_ValueRegistered() {
            registry.register(KEY_A, "v");
            assertThat(registry.isRegistered(KEY_A)).isTrue();
        }

        @Test
        @DisplayName("returns false for unknown key")
        void should_ReturnFalse_When_KeyUnknown() {
            assertThat(registry.isRegistered(KEY_A)).isFalse();
        }

        @Test
        @DisplayName("returns false after unregister")
        void should_ReturnFalse_After_Unregister() {
            registry.register(KEY_A, "v");
            registry.unregister(KEY_A);
            assertThat(registry.isRegistered(KEY_A)).isFalse();
        }
    }

    // =========================================================================
    // unregister
    // =========================================================================
    @Nested
    @DisplayName("unregister()")
    class Unregister {

        @Test
        @DisplayName("returns the removed value")
        void should_ReturnValue_When_Unregistered() {
            // given
            registry.register(KEY_A, "v");

            // when
            String removed = registry.unregister(KEY_A);

            // then
            assertThat(removed).isEqualTo("v");
        }

        @Test
        @DisplayName("returns null for unknown key")
        void should_ReturnNull_When_KeyUnknown() {
            assertThat(registry.unregister(KEY_A)).isNull();
        }
    }

    // =========================================================================
    // get / find
    // =========================================================================
    @Nested
    @DisplayName("get() / find()")
    class Retrieval {

        @Test
        @DisplayName("get() returns null for missing key")
        void should_ReturnNull_When_KeyMissing() {
            assertThat(registry.get(KEY_A)).isNull();
        }

        @Test
        @DisplayName("find() wraps existing value in Optional")
        void should_ReturnPresentOptional_When_ValueExists() {
            registry.register(KEY_A, "v");
            assertThat(registry.find(KEY_A)).contains("v");
        }

        @Test
        @DisplayName("find() returns empty Optional for missing key")
        void should_ReturnEmptyOptional_When_KeyMissing() {
            assertThat(registry.find(KEY_A)).isEmpty();
        }
    }

    // =========================================================================
    // getAll / getMap / size
    // =========================================================================
    @Nested
    @DisplayName("collection views")
    class CollectionViews {

        @Test
        @DisplayName("getAll() contains all registered values")
        void should_ContainAll_When_MultipleRegistered() {
            registry.register(KEY_A, "a");
            registry.register(KEY_B, "b");

            Collection<String> all = registry.getAll();
            assertThat(all).containsExactlyInAnyOrder("a", "b");
        }

        @Test
        @DisplayName("getMap() is unmodifiable")
        void should_ReturnUnmodifiableMap() {
            registry.register(KEY_A, "a");
            Map<TypedKey<? extends String>, String> map = registry.getMap();
            assertThatThrownBy(() -> map.put(KEY_B, "b"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("size() reflects current entry count")
        void should_TrackSize_Correctly() {
            assertThat(registry.size()).isZero();
            registry.register(KEY_A, "a");
            assertThat(registry.size()).isEqualTo(1);
            registry.register(KEY_B, "b");
            assertThat(registry.size()).isEqualTo(2);
            registry.unregister(KEY_A);
            assertThat(registry.size()).isEqualTo(1);
        }
    }

    // =========================================================================
    // clear
    // =========================================================================
    @Nested
    @DisplayName("clear()")
    class Clear {

        @Test
        @DisplayName("empties the registry")
        void should_EmptyRegistry_When_Cleared() {
            registry.register(KEY_A, "a");
            registry.register(KEY_B, "b");

            registry.clear();

            assertThat(registry.size()).isZero();
            assertThat(registry.getAll()).isEmpty();
        }
    }

}