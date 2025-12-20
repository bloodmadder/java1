import java.util.Arrays;

// Абстрактный класс List

abstract class MyList<T> {
    public abstract T get(int index);
    public abstract void set(int index, T value);
    public abstract int size();
    public abstract void add(T value);
    public abstract T remove(int index);
    public abstract void clear();
    public abstract boolean contains(T value);

    public boolean isEmpty() {
        return size() == 0;
    }

    protected void checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("Index %d out of bounds for size %d", index, size)
            );
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i < size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

// Класс Node (узел) для LinkedList
class Node<T> {
    T value;
    Node<T> next;

    Node(T value) {
        this.value = value;
        this.next = null;
    }

    Node(T value, Node<T> next) {
        this.value = value;
        this.next = next;
    }
}

// Реализация ArrayList

class MyArrayList<T> extends MyList<T> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final double EXPAND_THRESHOLD = 0.8;

    private Object[] array;
    private int size;

    public MyArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid initial capacity: " + initialCapacity);
        }
        this.array = new Object[initialCapacity];
        this.size = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index, size);
        return (T) array[index];
    }

    @Override
    public void set(int index, T value) {
        checkIndex(index, size);
        array[index] = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(T value) {
        ensureCapacity();
        array[size++] = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index, size);
        T removed = (T) array[index];

        // Сдвигаем элементы
        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }

        array[--size] = null; // Помогаем сборщику мусора
        return removed;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean contains(T value) {
        for (int i = 0; i < size; i++) {
            if (array[i] == value || (array[i] != null && array[i].equals(value))) {
                return true;
            }
        }
        return false;
    }

    private void ensureCapacity() {
        if (array.length == 0) {
            array = new Object[DEFAULT_CAPACITY];
            return;
        }

        if ((double) size / array.length >= EXPAND_THRESHOLD) {
            int newCapacity = array.length * 2;
            if (newCapacity < 0) newCapacity = Integer.MAX_VALUE;

            Object[] newArray = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newArray[i] = array[i];
            }
            array = newArray;
            System.out.println("Array expanded to " + newCapacity);
        }
    }

    public void add(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        ensureCapacity();

        // Сдвигаем элементы вправо
        for (int i = size; i > index; i--) {
            array[i] = array[i - 1];
        }

        array[index] = value;
        size++;
    }

    public int indexOf(T value) {
        for (int i = 0; i < size; i++) {
            if (array[i] == value || (array[i] != null && array[i].equals(value))) {
                return i;
            }
        }
        return -1;
    }

    public int capacity() {
        return array.length;
    }
}

// Реализация LinkedList
class MyLinkedList<T> extends MyList<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public T get(int index) {
        checkIndex(index, size);
        Node<T> current = getNodeAt(index);
        return current.value;
    }

    @Override
    public void set(int index, T value) {
        checkIndex(index, size);
        Node<T> node = getNodeAt(index);
        node.value = value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(T value) {
        addLast(value);
    }

    @Override
    public T remove(int index) {
        checkIndex(index, size);

        if (index == 0) {
            return removeFirst();
        }

        Node<T> prev = getNodeAt(index - 1);
        Node<T> toRemove = prev.next;
        T value = toRemove.value;

        prev.next = toRemove.next;
        if (toRemove == tail) {
            tail = prev;
        }

        size--;
        return value;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean contains(T value) {
        Node<T> current = head;
        while (current != null) {
            if (current.value == value || (current.value != null && current.value.equals(value))) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private Node<T> getNodeAt(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void add(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (index == 0) {
            addFirst(value);
        } else if (index == size) {
            addLast(value);
        } else {
            Node<T> prev = getNodeAt(index - 1);
            Node<T> newNode = new Node<>(value, prev.next);
            prev.next = newNode;
            size++;
        }
    }

    public T removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("List is empty");
        }

        T value = head.value;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return value;
    }

    public T removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException("List is empty");
        }

        if (size == 1) {
            return removeFirst();
        }

        Node<T> prev = getNodeAt(size - 2);
        T value = tail.value;
        tail = prev;
        tail.next = null;
        size--;
        return value;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("List is empty");
        }
        return head.value;
    }

    public T getLast() {
        if (isEmpty()) {
            throw new IllegalStateException("List is empty");
        }
        return tail.value;
    }

    public int indexOf(T value) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (current.value == value || (current.value != null && current.value.equals(value))) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }
}

/**
 * Демонстрационный класс
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== TESTING ARRAYLIST ===");
        testArrayList();

        System.out.println("\n=== TESTING LINKEDLIST ===");
        testLinkedList();
    }

    private static void testArrayList() {
        MyArrayList<Integer> list = new MyArrayList<>(5);
        System.out.println("Created ArrayList with capacity: " + list.capacity());

        // Добавляем элементы
        for (int i = 1; i <= 15; i++) {
            list.add(i * 10);
            System.out.printf("Added %d, size: %d, capacity: %d\n",
                    i * 10, list.size(), list.capacity());
        }

        System.out.println("\nArrayList contents: " + list);
        System.out.println("Element at index 3: " + list.get(3));

        list.set(3, 999);
        System.out.println("After set at index 3: " + list.get(3));

        list.add(2, 555);
        System.out.println("After insert at index 2: " + list);

        int removed = list.remove(5);
        System.out.println("Removed element at index 5: " + removed);
        System.out.println("After removal: " + list);

        System.out.println("Contains 555? " + list.contains(555));
        System.out.println("Index of 999: " + list.indexOf(999));
    }

    private static void testLinkedList() {
        MyLinkedList<String> list = new MyLinkedList<>();

        list.add("First");
        list.add("Second");
        list.add("Third");
        list.addFirst("Zero");
        list.addLast("Fourth");

        System.out.println("LinkedList: " + list);
        System.out.println("Size: " + list.size());
        System.out.println("First: " + list.getFirst());
        System.out.println("Last: " + list.getLast());

        list.add(2, "Inserted");
        System.out.println("After insert at index 2: " + list);

        System.out.println("Removed first: " + list.removeFirst());
        System.out.println("After remove first: " + list);

        System.out.println("Removed last: " + list.removeLast());
        System.out.println("After remove last: " + list);

        String removed = list.remove(1);
        System.out.println("Removed at index 1: " + removed);
        System.out.println("After remove at index 1: " + list);

        System.out.println("Contains 'Second'? " + list.contains("Second"));
        System.out.println("Index of 'Third': " + list.indexOf("Third"));
    }

}
