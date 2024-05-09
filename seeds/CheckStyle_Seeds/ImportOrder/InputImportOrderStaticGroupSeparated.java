/*
ImportOrder
option = top
groups = java, org
ordered = (default)true
separated = true
separatedStaticGroups = (default)false
caseSensitive = (default)true
staticGroups = (default)
sortStaticImportsAlphabetically = (default)false
useContainerOrderingForStatic = (default)false
tokens = (default)STATIC_IMPORT


*/


// ok

// violation

// violation
// ok

public class InputImportOrderStaticGroupSeparated {
  void method() {}
}
