/** Comment required test class. */
class CommentRequired {
  /** Creates a new instance of comment required. */
  public CommentRequired() {
    Object o =
        new Object() {
          /** {@inheritDoc} */
          public String toString() {
            return "Inner Class";
          }
        };
  }
}
