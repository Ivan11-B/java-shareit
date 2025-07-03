package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ItemOwnershipException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item createItem(Item item, Long userId) {
        User user = validateUserById(userId);
        item.setOwner(user);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long userId) {
        Item currentItem = getItemById(item.getId());
        if (!currentItem.getOwner().getId().equals(userId)) {
            throw new ItemOwnershipException("Пользователь не является владельцем данного товара");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            currentItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            currentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            currentItem.setAvailable(item.getAvailable());
        }
        return currentItem;
    }

    @Override
    public List<ItemWithBookingsDto> findAll(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream().map(item -> {
            List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(item.getId());
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            return itemMapper.toDto(item, bookings, comments);
        }).collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь ID= " + id + " не найдена!"));
    }

    @Override
    public ItemWithBookingsDto getItemByIdWithBooking(Long id) {
        Item item = getItemById(id);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(item.getId());
        List<Comment> comments = commentRepository.findByItemId(id);
        return itemMapper.toDto(item, bookings, comments);
    }

    @Override
    public List<Item> searchByString(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchInNameOrDescription(text);

    }

    @Override
    @Transactional
    public Comment addComment(Comment comment, Long userId, Long itemId) {
        User author = validateUserById(userId);
        Item item = getItemById(itemId);
        if (!hasValidBooking(userId, itemId)) {
            throw new IllegalStateException("Пользователь не бронировал данную вещь");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        return commentRepository.save(comment);
    }

    boolean hasValidBooking(Long userId, Long itemId) {
        return bookingRepository.existsByBookerIdAndItemIdAndBookingStatusAndEndBefore(
                userId,
                itemId,
                BookingStatus.APPROVED,
                LocalDateTime.now());
    }

    private User validateUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User ID= " + id + " не найден!"));
    }
}