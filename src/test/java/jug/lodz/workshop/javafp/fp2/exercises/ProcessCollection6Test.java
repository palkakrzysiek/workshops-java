package jug.lodz.workshop.javafp.fp2.exercises;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pwlodarski on 2016-03-09.
 */
public class ProcessCollection6Test {

    @Test
    public void testLinesToUsers() throws Exception {
        List<String> input = Arrays.asList("user1,tv,3000,01-02-2016", "user2,tv,3000,02-02-2016", "user1,console,1500,02-02-2016");

        Collection<String> output = ProcessCollection6.linesToUsers.apply(input);

        assertThat(output).containsExactly("user1","user2","user1");
    }

    @Test
    public void testLiftToCollection() throws Exception {
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        Function<Collection<Integer>, Collection<Integer>> lifted = FunctionalLibraryV1.liftToCollection((Integer x) -> x + 1);

        Collection<Integer> output = lifted.apply(input);

        assertThat(output).containsExactly(2,3,4,5,6);
    }


    @Test
    public void testLiftToOptional() throws Exception {
        Optional<String> some = Optional.of("someText");
        Optional<String> none = Optional.empty();

        Function<Optional<String>, Optional<Integer>> convertToLenghts = FunctionalLibraryV1.liftToOptional((String s) -> s.length());

        assertThat(convertToLenghts.apply(some)).contains(8);
        assertThat(convertToLenghts.apply(none)).isEmpty();
    }
}
