package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;

    private static User getUser(String email) {
        return User.builder()
                .name("Дима")
                .email(email)
                .build();
    }

    private static Item getItem(User owner) {
        return Item.builder()
                .name("Грабли")
                .description("Грабли для уборки листвы")
                .available(true)
                .owner(owner)
                .build();
    }

    private static Booking getBooking(Item item, User booker) {
        return Booking.builder()
                .status(BookingStatus.WAITING)
                .startDate(LocalDateTime.now().plusMinutes(5))
                .endDate(LocalDateTime.now().plusDays(10))
                .item(item)
                .booker(booker)
                .build();
    }

    @Test
    void findBookingShouldReturnNotNullBooking() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);

        Booking result = bookingRepository.findBooking(booking.getId(), userTwo.getId()).orElse(null);

        assertThat(result, allOf(
                hasProperty("id", notNullValue()),
                hasProperty("item", notNullValue()),
                hasProperty("booker", equalTo(userTwo))
        ));
    }

    @Test
    void findBookingShouldReturnEmptyResultWhenBookingNotFound() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);

        Long resultId = booking.getId();
        Optional<Booking> result = bookingRepository.findBooking(++resultId, userTwo.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingByOwnerWhenNotOwner() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        Optional<Booking> result = bookingRepository.findBookingByOwner(booking.getId(), userTwo.getId());
        assertThat(result, equalTo(Optional.empty()));
    }

    @Test
    void findBookingByOwnerWhenOwner() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        Optional<Booking> result = bookingRepository.findBookingByOwner(booking.getId(), userOne.getId());
        assertThat(result, not(equalTo(Optional.empty())));
    }

    @Test
    void findBookingByOwnerShouldReturnEmptyResultWhenOwnerIdIncorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        Optional<Booking> result = bookingRepository.findBookingByOwner(booking.getId(), userTwo.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findBookingByOwnerShouldReturnEmptyResultWhenBookingIdIncorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        Optional<Booking> result = bookingRepository.findBookingByOwner(100L, userOne.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findLastBookingByItemIdShouldReturnLastBookingWhenItemIdIsCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setStartDate(LocalDateTime.now().minusDays(1L));
        testEntityManager.persist(booking);
        Booking nextBooking = bookingRepository.findLastBookingByItemId(item.getId(), LocalDateTime.now()).get();
        assertThat(nextBooking, allOf(
                hasProperty("id", equalTo(booking.getId()))
        ));
    }

    @Test
    void findLastBookingByItemIdShouldReturnEmptyResultWhenItemIdIsIncorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setStartDate(LocalDateTime.now().minusDays(1L));
        testEntityManager.persist(booking);
        Optional<Booking> nextBooking = bookingRepository.findLastBookingByItemId(100L, LocalDateTime.now());
        assertTrue(nextBooking.isEmpty());
    }

    @Test
    void findNextBookingByItemIdShouldReturnFutureBookingWhenItemIdIsCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);

        Booking nextBooking = bookingRepository.findNextBookingByItemId(item.getId(), LocalDateTime.now()).get();

        assertThat(nextBooking, allOf(
                hasProperty("id", equalTo(booking.getId()))
        ));
    }

    @Test
    void findNextBookingByItemIdShouldReturnEmptyResultWhenItemIdIsInCorrect() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        Optional<Booking> nextBooking = bookingRepository.findNextBookingByItemId(100L, LocalDateTime.now());
        assertTrue(nextBooking.isEmpty());
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndDateBeforeShouldReturnBooking() {
        LocalDateTime now = LocalDateTime.now();
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setEndDate(now.minusHours(1));
        testEntityManager.persist(booking);
        Booking result = bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(userTwo.getId(), item.getId(), now)
                .orElse(null);
        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("startDate", notNullValue()),
                hasProperty("endDate", notNullValue()),
                hasProperty("booker", equalTo(userTwo)),
                hasProperty("item", equalTo(item)),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        ));
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndDateBeforeShouldReturnEmptyResultWhenItemIdIncorrect() {
        LocalDateTime now = LocalDateTime.now();
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        booking.setEndDate(now.minusHours(1));
        testEntityManager.persist(booking);
        Optional<Booking> result = bookingRepository.findFirstByBookerIdAndItemIdAndEndDateBefore(userTwo.getId(),
                100L, now);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByItemIdInShouldReturnBookings() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(List.of(item.getId()));
        assertThat(bookings, hasSize(1));
        assertThat(bookings, hasItem(booking));
    }

    @Test
    void findAllByItemIdInShouldReturnEmptyListWhenBookingsNotFound() {
        User userOne = getUser("dima@yandex.ru");
        User userTwo = getUser("fima@yandex.ru");
        testEntityManager.persist(userOne);
        testEntityManager.persist(userTwo);

        Item item = getItem(userOne);
        testEntityManager.persist(item);

        Booking booking = getBooking(item, userTwo);
        testEntityManager.persist(booking);
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(Collections.emptyList());
        assertThat(bookings, empty());
    }
}