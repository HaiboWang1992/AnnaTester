import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * This class centralizes the parsing of the URL parameters from the search for the
 * &amp;requiredfields= and the &amp;newrequiredfields= parameters.
 *
 * <p>Because this was being done in 3 different places in the code it was important to centralize
 * it. These parameters may contain international characters which need to be consistency decoded.
 * These parameters are also used to generate the URL for 1 - the tree, 2 - The breadcrumbs, 3 - The
 * Sort By links.
 *
 * @author Peter Lenahan
 */
public class Bug3559113 {
  private static final Logger magnifySearchLogger = Logger.getLogger("ibi.search.search");

  private final ArrayList<String> fieldnames = new ArrayList<String>();
  private final ArrayList<String> fieldvalues = new ArrayList<String>();

  @SuppressWarnings("unchecked")
  public ArrayList<String> getFields() {
    return (ArrayList<String>) fieldnames.clone();
  }

  @SuppressWarnings("unchecked")
  public ArrayList<String> getValues() {
    return (ArrayList<String>) fieldvalues.clone();
  }
  /**
   * This constructor parses the URL parameters and builds the tables. It parses the requiredfields
   * and newrequiredfields parameters on the URL. then saves them in 2 tables which are accessed via
   * the methods getFields() and getValues(). The data stored in the table has had all the decoding
   * applied.
   *
   * <p>The data stored in the requiredfields= parameter must be double encoded because it can
   * contain a "." or a "|" which are the google and/or logical operations to apply to the query. We
   * only support the "." and operation.
   *
   * <p>while the data stored in the newrequiredfields parameter can be singular decoded
   *
   * @param req the request object of the servlet
   */
  public Bug3559113(final HttpServletRequest req) {
    String reqfieldParameter = req.getParameter("requiredfields");
    if (reqfieldParameter != null) {

      // Decode the data after you split the expression apart

      // String reqfields=decode(reqfieldParameter);
      String reqfields = reqfieldParameter;
      //       try {
      //           reqfields=new String(reqfieldParameter.getBytes(),"utf8");
      //       } catch (UnsupportedEncodingException e) {
      //
      //           e.printStackTrace();
      //       }

      String[] ss_and = null;
      String[] ss_or = null;
      if (reqfields != null) {
        if (reqfields.indexOf('|') > -1) ss_or = reqfields.split("\\|");
        else ss_and = reqfields.split("\\.");

        String[] ss_l = ss_or;
        // This is what we may be parsing
        // The Meta Tags can be separated by either a "." for and "and" operation
        // of a "|" for and "or" operation.
        //
        //   requiredfields=MetaName1:MetaValue1.MetaName2:MetaValue2.MetaName3:MetaValue3 ...
        // or
        //   requiredfields=MetaName1:MetaValue1|MetaName2:MetaValue2|MetaName3:MetaValue3 ...
        //
        for (int Logical_operator = 1; Logical_operator < 3; Logical_operator++) {
          // parse both the or case as well as the and case
          if (ss_l == null) {
            ss_l = ss_and;
            continue;
          }
          final int len = ss_l.length;
          for (int l_index = 0; l_index < len; l_index++) {
            final String[] ss = decode(ss_l[l_index]).split(":");
            if (ss.length > 1) {
              // First we must separate the multiple keys if they exist.
              addField(
                  ss[0], // String fieldname,
                  ss[1]); // String fieldvalue,
            }
          }
          ss_l = ss_and;
        }
      }
    }
    // Because of UTF-8 issues this is double encoded in the browser.
    // So decode it before it can be used
    String newRequiredFields = req.getParameter("newrequiredfields");
    if (newRequiredFields != null) {
      String[] newrequiredfields = decode(req.getParameter("newrequiredfields")).split(":");
      // If the URL is chopped off because it exceeds the browser limit of 2000 characters, then
      // the ":" may not be there, check to be sure that there is actually 2 items.
      if (newrequiredfields != null && newrequiredfields.length > 1) {

        addField(
            newrequiredfields[0], // String fieldname,
            newrequiredfields[1]); // String fieldvalue
      }
    }
  }

  /**
   * This method does not decode the data, it must be called with the data already decoded correctly
   * by the caller
   *
   * @param fieldname Which is added to the required fieldnames array
   * @param fieldvalue Which is added to the required fieldvalues array
   */
  private void addField(String fieldname, String fieldvalue) {
    String decodedfieldname = fieldname;
    String decodedfieldvalue = fieldvalue;

    if (magnifySearchLogger.isLoggable(Level.FINER))
      magnifySearchLogger.finer(
          "RequiredFields():adecodedfieldnameddField: decodedfieldname="
              + decodedfieldname
              + ", decodedfieldvalue= "
              + decodedfieldvalue);
    fieldnames.add(fieldname);
    fieldvalues.add(fieldvalue);
  }
  /**
   * @param s
   * @return
   */
  private String decode(final String s) {
    if (s == null) return null;
    try {
      return java.net.URLDecoder.decode(s, "UTF8");
    } catch (UnsupportedEncodingException ex) {
      if (magnifySearchLogger.isLoggable(Level.FINER))
        magnifySearchLogger.finer("decode(): Use ibi.util.URLDecoder.decode()");
      //       return ibi.util.URLDecoder.decode(s);
      return s;
    }
  }
  /*
  private String decodeValue (final String s) {
      if (s == null)
          return null;
      return ibi.search.common.Encoders.requiredFieldsDecoder(s);

  }
  */
}
