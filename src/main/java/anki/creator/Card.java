package anki.creator;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

@Slf4j
public class Card {
    private int ord;
    private boolean suspend;

    public Card(int ord, boolean suspend) {
        this.ord = ord;
        this.suspend = suspend;
    }

    public void writeToDb(Connection conn, long timestamp, long deckId, long noteId, Iterator<Long> idGen, int due) throws SQLException {
        String sql = "INSERT INTO cards VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        int queue = suspend ? -1 : 0;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            long cardId = idGen.next();

            pstmt.setLong(1, cardId);   // id
            pstmt.setLong(2, noteId);   // nid
            pstmt.setLong(3, deckId);   // did
            pstmt.setInt(4, ord);       // ord
            pstmt.setLong(5, timestamp);// mod
            pstmt.setInt(6, -1);        // usn
            pstmt.setInt(7, 0);         // type (=0 for non-Cloze)
            pstmt.setInt(8, queue);     // queue
            pstmt.setInt(9, due);       // due
            pstmt.setInt(10, 0);        // ivl
            pstmt.setInt(11, 0);        // factor
            pstmt.setInt(12, 0);        // reps
            pstmt.setInt(13, 0);        // lapses
            pstmt.setInt(14, 0);        // left
            pstmt.setInt(15, 0);        // odue
            pstmt.setInt(16, 0);        // odid
            pstmt.setInt(17, 0);        // flags
            pstmt.setString(18, "");   // data

            log.info("Executing SQL: {} with values: id={}, noteId={}, deckId={}, ord={}, timestamp={}, queue={}, due={}",
                    sql, cardId, noteId, deckId, ord, timestamp, queue, due);

            pstmt.executeUpdate();
            log.info("Card inserted successfully with id: {}", cardId);
        } catch (SQLException e) {
            log.error("Error inserting card into database", e);
            throw e;
        }
    }
}