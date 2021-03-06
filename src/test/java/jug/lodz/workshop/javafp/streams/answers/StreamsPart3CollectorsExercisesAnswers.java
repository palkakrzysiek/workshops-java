package jug.lodz.workshop.javafp.streams.answers;

import jug.lodz.workshop.javafp.streams.Transactions;
import org.junit.Test;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.*;
import static jug.lodz.workshop.javafp.streams.answers.StreamsPart3CollectorsExercisesAnswers.NUMBER_CHARACTERISTIC.EVEN;
import static jug.lodz.workshop.javafp.streams.answers.StreamsPart3CollectorsExercisesAnswers.NUMBER_CHARACTERISTIC.ODD;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pawel on 16.06.16.
 */
public class StreamsPart3CollectorsExercisesAnswers {

    /**
     * 1) Define proper delimiter, prefix and suffix
     * 2) join all elements into one string
     */
    @Test
    public void JoinIntoString() throws Exception {
        String result = Stream.of("java8", "scala", "kotlin")
                .collect(Collectors.joining(",", "[ Languages:", " ]"));

        assertThat(result).isEqualToIgnoringCase("[ Languages:java8,scala,kotlin ]");
    }


    /**
     * 1) Collect all elements to TreeSet
     */
    @Test
    public void collectToTreeSet() throws Exception {
        Set<String> result = Stream.of("java8", "scala", "kotlin").collect(Collectors.toCollection(TreeSet::new));
        assertThat(result).isInstanceOf(TreeSet.class).hasSize(3);
    }

    @Test
    /**
     * 1) use Collectors.averagingInt to count average
     * 2) pass in an "identity" function
     */
    public void countAverage() throws Exception {
        Double average = Stream.of(10, 20, 30,40).collect(Collectors.averagingInt(i -> i));

        assertThat(average).isEqualTo(25.0);
    }

    /**
     *  1) use "partitionBy" and "isNumeric" to split stream into two map entries
     */
    @Test
    public void partitionToDigitAndNotDigit() throws Exception {
        Map<Boolean, List<String>> result = Stream.of("1", "a", "bbb", "20", "66", "lodz", "jug", "1000")
                .collect(Collectors.partitioningBy(this::isNumeric));

        assertThat(result)
                .containsEntry(true, asList("1","20","66","1000"))
                .containsEntry(false,asList("a","bbb","lodz","jug"));

    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    //Level 2

    /**
     *  in collect method
     *  1) use "mapping" to map String into Integer
     *  2) use "maxBy" Collector
     */
    @Test
    public void collectNumeric() throws Exception {
        Optional<Integer> max = Stream.of("1", "a", "bbb", "20", "66", "lodz", "jug", "1000")
                .filter(this::isNumeric)
                .collect(mapping(Integer::parseInt,
                        Collectors.maxBy(Integer::compare)
                ));

        assertThat(max).hasValue(1000);


    }

    enum NUMBER_CHARACTERISTIC{EVEN,ODD}

    /**
     *
     *  1) Group by checking if number is EVEN or ODD
     *  2) map to string
     *  3) join
     */
    @Test
    public void groupAndJoinNumbers() throws Exception {
        Map<NUMBER_CHARACTERISTIC, String> result = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .collect(
                        groupingBy(i -> i % 2 == 0 ? EVEN : ODD,
                                mapping(i -> i.toString(),
                                        joining(","))
                        ));

        assertThat(result)
                .containsEntry(EVEN,"2,4,6,8")
                .containsEntry(ODD,"1,3,5,7,9");
    }


     //partition by i grouping by

    /**
     *
     */
    @Test
    public void groupTransactions() throws Exception {
        Map<Boolean, Map<Integer, List<Integer>>> result = readTransactions().collect(
                partitioningBy(t -> t.amount > 150, groupingBy(t -> t.accountFrom, mapping(t -> t.id, toList())))
        );

//        System.out.println(result); //check the result


        assertThat(result.get(true)).containsEntry(1, asList(2)).containsEntry(3,asList(5,9));
        assertThat(result.get(false)).containsEntry(1, asList(1,7,8)).containsEntry(2,asList(3,6));
    }


    //Level 3 - custom collector


    @Test
    public void testCustomCollector() throws Exception {
        List<Integer> result = Stream.of(1, 2, 3, 4, 5).collect(new CustomToLinkedListCollector<>());

        assertThat(result).containsExactly(1,2,3,4,5);
    }

    private Stream<Transactions.FlatTransaction> readTransactions() {
        return Transactions.transactions.stream()
                .skip(1)  // KATA
                .map(line -> line.split(","))
                .filter(cels->cels.length==5) // KATA
                .filter(cels->!cels[0].equals("aaa") && !cels[1].equals("bbb")) // KATA
                .filter(cels->!cels[0].isEmpty())  // KATA
                .map(Transactions.FlatTransaction::new);
    }
}


/**
 *
 * @param <T> - item type
 * first List<T> - accumulator type
 * second List<T> - final type
 *
 *
 *  1) In supplier create a new list
 *  2) In accumulator add a new element to the list
 *  3) In Finisher use identity
 *  4) In combiner merge two lists
 *  5) In characteristics return Characteristics.IDENTITY_FINISH and  Characteristics.CONCURRENT
 *
 */
class CustomToLinkedListCollector<T> implements Collector<T,List<T>,List<T>>{

    @Override
    public Supplier<List<T>> supplier() {
        return LinkedList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
            return (l1,l2)->{
                l1.addAll(l2);
                return l1;
            };
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(
                Characteristics.IDENTITY_FINISH, Characteristics.CONCURRENT));
    }
}



