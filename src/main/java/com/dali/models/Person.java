package com.dali.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vavr.Lazy;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Created by dali on 22/06/17.
 */
@JsonPropertyOrder({"id", "name", "age", "email", "country"})
@JsonIgnoreProperties({"composedName", "emailTag"})
public class Person {
    public final String id;
    public final String name;
    public final int age;
    public final String email;
    public final String country;

    @JsonValue
    public String getId() {
        return id;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonValue
    public int getAge() {
        return age;
    }

    @JsonValue
    public String getEmail() {
        return email;
    }

    @JsonValue
    public String getCountry() {
        return country;
    }


    @JsonCreator
    public Person(@JsonProperty("id") final String id, @JsonProperty("name") final String name, @JsonProperty("age") final int age, @JsonProperty("email") final String email, @JsonProperty("country") final String country) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.country = country;
    }


    public static void main(String[] args) {

//        String content = "";
//        try {
//            File file = ResourceUtils.getFile("classpath:person.json");
//            content = new String(Files.readAllBytes(file.toPath()));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String content = Person.readJsonFile("person.json").getOrElse("");

        Person person = new Person("594bd3ffd4521vb03de57c0a", "undefined undefined", 27, "undefined.undefined@gmail.com", "space");

        //################  json to object
        final java.util.List<Person> persons0 = person.fromJsonList(content);

        final Either<Throwable, List<Person>> persons1 = person.fromJson2EitherListVavr(content);
        final Try<Person[]> persons2 = person.fromJsonTryListVavr(content);
        final Try<List<Person>> persons3 = person.fromJson2TryListVavr(content);
        final Either<Throwable, Person[]> persons4 = person.fromJsonEitherListVavr(content);
        //################

        persons1.forEach(s -> {
            System.out.println(s);
        });
        //####################
//        System.out.println((person.divide(4, 0).getOrElse(0)));
//        System.out.println((person.divide(4, 2).getOrElse(0)));

//        System.out.println((person.divide2(4, 0)));
        //####################


    }


    @Override
//    public String toString() {
//        return "Person{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", age=" + age +
//                ", email='" + email + '\'' +
//                ", country='" + country + '\'' +
//                '}';
//    }

    public String toString() {
        return  id + ';' + name + ';' + age + ';' + email + ';' + country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        if (!id.equals(person.id)) return false;
        if (!name.equals(person.name)) return false;
        if (!email.equals(person.email)) return false;
        return country.equals(person.country);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + age;
        result = 31 * result + email.hashCode();
        result = 31 * result + country.hashCode();
        return result;
    }

    public Try<Integer> divide(Integer dividend, Integer divisor) {
        return Try.of(() -> dividend / divisor);
    }

    public Integer divide2(Integer dividend, Integer divisor) {
        try {
            return dividend / divisor;
        } catch (ArithmeticException e) {
            return 0;
        }
    }


    public List<List<String>> readCsvVavr(String filename) {
        return List.ofAll(
                Try.of(() -> Files.lines(new File("src/main/resources/" + filename).toPath()).collect(Collectors.toList())).getOrElseGet(err -> {
                    err.printStackTrace();
                    return new ArrayList<>();
                })
        ).map(line -> List.of(line.split(";")));
    }


    public static Try<String> readJsonFile(String fileName) {
        return Try.of(
                () -> new String(Files.readAllBytes(ResourceUtils.getFile("classpath:" + fileName).toPath()))
        );
    }
    /////////////////////////////////////
    // CLASSIC

    /**
     * @return
     */
    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        StringWriter stringPerson = new StringWriter();
        try {
            objectMapper.writeValue(stringPerson, this);
            return stringPerson.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    // JAVASLANG

    /**
     * @return
     */
    public Either<Throwable, String> toJsonEither() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> Either.<Throwable, String>right(mapper.writer().writeValueAsString(List.of(this))))
                .getOrElseGet(err -> Either.left(err));
    }

    // JAVASLANG

    /**
     * @return
     */
    public Try<String> toJsonTry() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> mapper.writer().writeValueAsString(List.of(this)));
    }
    /////////////////////////////////////

    /////////////////////////////////////

    // CLASSIC

    /**
     * @param jsonData
     * @return
     */
    public static Person fromJson(byte[] jsonData) {
        try {
            return new ObjectMapper().readValue(jsonData, Person.class);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Either<Throwable, Person> fromJsonEitherVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> Either.<Throwable, Person>right(mapper.readValue(jsonValue, Person.class)))
                .getOrElseGet(err -> Either.left(err));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Either<Throwable, Person> fromJson2EitherVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> Either.<Throwable, Person>right(mapper.readValue(jsonValue, new TypeReference<List<Person>>() {
        }))).getOrElseGet(err -> Either.left(err));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Try<Person> fromJsonTryVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> mapper.readValue(jsonValue, Person.class));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Try<Person> fromJson2TryVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> mapper.readValue(jsonValue, new TypeReference<List<Person>>() {
        }));
    }

    //CLASSIC

    /**
     * @param jsonValue
     * @return
     */
    public static java.util.List<Person> fromJsonList(String jsonValue) {
        try {
            return new ObjectMapper().readValue(jsonValue, new TypeReference<java.util.List<Person>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Either<Throwable, List<Person>> fromJson2EitherListVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> Either.<Throwable, List<Person>>right(mapper.readValue(jsonValue, new TypeReference<List<Person>>() {
        }))).getOrElseGet(err -> Either.left(err));
        //List<Person> myObjects = mapper.readValue(jsonValue, mapper.getTypeFactory().constructCollectionType(List.class, Person.class));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Try<List<Person>> fromJson2TryListVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> mapper.readValue(jsonValue, new TypeReference<List<Person>>() {
        }));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Try<Person[]> fromJsonTryListVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> mapper.readValue(jsonValue, Person[].class));
    }

    // JAVASLANG

    /**
     * @param jsonValue
     * @return
     */
    public Either<Throwable, Person[]> fromJsonEitherListVavr(String jsonValue) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return Try.of(() -> Either.<Throwable, Person[]>right(mapper.readValue(jsonValue, Person[].class))).getOrElseGet(err -> Either.left(err));
    }


//    public java.util.List<Person> fromJsonEitherListJava(String jsonValue) {
//        return fromJsonEitherListVavr(jsonValue).map(Value::toJavaList).toJavaList();
//    }
    /////////////////////////////////////


}
