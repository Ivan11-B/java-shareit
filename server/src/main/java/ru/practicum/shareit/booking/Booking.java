package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_start")
    private LocalDateTime start;

    @Column(name = "booking_end")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus bookingStatus;
}