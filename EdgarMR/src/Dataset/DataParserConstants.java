/* Generated By:JavaCC: Do not edit this line. DataParserConstants.java */
package Dataset;

public interface DataParserConstants {

  int EOF = 0;
  int COM = 6;
  int COM2 = 7;
  int RELATION = 8;
  int ATTRIBUTE = 9;
  int INPUTS = 10;
  int OUTPUTS = 11;
  int DATA = 12;
  int INTEG = 13;
  int REAL = 14;
  int EXP = 15;
  int COPENED = 16;
  int CCLOSED = 17;
  int CLOPENED = 18;
  int CLCLOSED = 19;
  int COLON = 20;
  int SCOLON = 21;
  int NULL = 22;
  int INT_CONST = 23;
  int BOOLEAN_CONST = 24;
  int REAL_CONST = 25;
  int CAD_CONST = 26;
  int IDENT = 27;
  int DIGIT = 28;
  int LETTER = 29;
  int ERR_LEX = 30;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\b\"",
    "\"\\n\"",
    "\"\\r\"",
    "<COM>",
    "<COM2>",
    "\"@relation\"",
    "\"@attribute\"",
    "\"@inputs\"",
    "\"@outputs\"",
    "\"@data\"",
    "\"integer\"",
    "\"real\"",
    "<EXP>",
    "\"[\"",
    "\"]\"",
    "\"{\"",
    "\"}\"",
    "\",\"",
    "\";\"",
    "<NULL>",
    "<INT_CONST>",
    "<BOOLEAN_CONST>",
    "<REAL_CONST>",
    "<CAD_CONST>",
    "<IDENT>",
    "<DIGIT>",
    "<LETTER>",
    "<ERR_LEX>",
  };

}
