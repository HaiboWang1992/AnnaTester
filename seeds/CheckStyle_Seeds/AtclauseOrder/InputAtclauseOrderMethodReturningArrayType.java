import java.sql.SQLException;

public interface InputAtclauseOrderMethodReturningArrayType {
  /**
   * @return int array
   * @exception SQLException
   * @throws java.sql.SQLTimeoutException
   * @see #addBatch // violation above
   * @see DatabaseMetaData#supportsBatchUpdates
   * @since 1.2
   */
  int[] executeBatch() throws SQLException;

  /**
   * {@code selected} is an array that will contain the indices of items that have been selected.
   *
   * @serial
   * @see #getSelectedIndexes() // violation
   * @see #getSelectedIndex() // violation
   */
  int selected[] = new int[0];
}
