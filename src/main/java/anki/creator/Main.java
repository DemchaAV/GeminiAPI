package anki.creator;

public class Main {
    public static void main(String[] args) {
        AnkiDatabaseInserter ankiDatabaseInserter = new AnkiDatabaseInserter(new Deck(956565L, "Abracadabra", "My first Test deck generation"));
        ankiDatabaseInserter.addSimpleNote("Сколько будет 2+2", "будет 4", new String[]{"math", "slojenie"});
        ankiDatabaseInserter.addSimpleNote("Сколько будет 3+8", "будет 11", new String[]{"math", "slojenie"});
        ankiDatabaseInserter.insertIntoDB("C:\\Users\\Demch\\OneDrive\\Рабочий стол\\Lerning\\Java\\anki test");
    }
}
