// TestReceiver.java (full implementation)
// DO NOT CHANGE THIS CLASS (except to add more debugging features if you wish)

/**
 * This is a test harness for the MessageReceiver class.
 * <P>
 * Note that the source code supplied is already complete.  It does NOT
 * need to be changed and won't be submitted for assessment.  You may,
 * if you wish, alter this class for the purpose of debugging your
 * implementation of MessageReceiver, but the interface to MessageReceiver
 * must not be changed. 
 * <P>
 * <B>Running the test harness under BlueJ</B>
 * <P>
 * Invoke one of the runTest methods by right clicking on the TestReceiver
 * class icon in the main BlueJ window.  These are static methods so you
 * don't need to create an object first.  Several versions of runTest
 * are available.  The easiest to use is runTest(), which sets all
 * options to sensible default values.  Other versions allow one or more
 * of the following options to be set:
 * <P>
 * mtu    - Specifies the physical layer maximum transfer unit (i.e.
 *          the length of the largest frame it can handle).  Versions
 *          of runTest() without this parameter use the default value
 *          defined in the class constant <I>defaultMtu</I>.
 * <P>
 * reveal - If true, causes control codes to be revealed.  This can help
 *          with the detection and debugging of string manipulation
 *          problems.  It's possible for an incorrectly generated string
 *          to contain binary control codes that aren't visible when
 *          displayed on the screen.  This option causes the text
 *          "(CONTROL)" to appear in place of any such control code
 *          found in a message string returned by receiveMessage.  Note
 *          that the presence of a control code doesn't necessarily mean
 *          there's a bug in your software, as messages are allowed to
 *          contain them.  Defaults to false in versions of runTest()
 *          without this parameter.
 * <P>
 * quiet  - If true, sets "quiet mode".  This suppresses the display of
 *          prompts and status information.  It's intended for use when
 *          the input and/or the output are being redirected (e.g. when
 *          using test data held in a file).  This option is of limited
 *          use when running under BlueJ.  Defaults to false in versions
 *          of runTest() without this parameter.
 * <P>
 * <B>Running the test harness from a command line</B>
 * <P>
 * This test harness can also be executed directly from the command line
 * (e.g. in a DOS prompt or Linux/Unix shell window), viz:
 * <P>
 *      java TestReceiver options
 * <P>
 * The following command line arguments are supported (see BlueJ section
 * above for full explanations):
 * <P>
 * -mN  Sets mtu to N.  Defaults to class constant defaultMtu if omitted.<BR>
 * -c   Reveal control codes.  Option disabled if omitted.<BR>
 * -q   Selects quiet mode.  Option disabled if omitted.
 * <P>
 * <B>Test harness operation</B>
 * <P>
 * When the test harness starts, it invokes the receiveMessage method
 * of the MessageReceiver class and passes the message it returns to
 * standard output (normally the screen / terminal window).
 * <P>
 * If receiveMessage is working correctly, it will invoke the
 * receiveFrame method of the physical layer class FrameReceiver one
 * or more times.  Each time receiveFrame is invoked, it reads a line
 * of text representing a single frame from standard input (normally
 * the keyboard).  Under BlueJ this appears in the terminal window.
 * receiveMessage should continue to read frames until it detects the
 * last frame of a message or the physical end of the input stream has
 * been reached (more on this below).  receiveMessage should reassemble
 * the message extracted from the sequence of frames and return it.
 * <P>
 * receiveFrame signals the end of the input stream has been reached
 * by returning null.  It will do this when the user enters a string
 * matching that defined in class constant <I>stop</I> or the physical
 * end of the input stream is reached when input is redirected from a
 * file.
 * <P>
 * <B>Additional notes when running from a command line</B>
 * <P>
 * Note that Java's standard keyboard input and screen output packages
 * are designed to handle plain text (i.e. "printable" characters like
 * those appearing on the keyboard).  Frames strings containing certain
 * control codes may therefore not be handled correctly.  For example,
 * redirecting input from a binary file (e.g. an MP3 file) is unlikely
 * to produce correct results.  This is a limitation of the test harness
 * and physical layer implementations, not the MessageReceiver class.
 * <P>
 * The test harness will also terminate prematurely if input has been
 * redirected from a file containing a line that matches the value of
 * class constant <I>stop</I>.  This restriction can be overcome by
 * setting <I>stop</I> to null.
 */

public class TestReceiver
{
    // Fields ----------------------------------------------------------
  
    private static final int defaultMtu = 20;  // default value for MTU
                                               // leaves room for 10 message chars
    private static final String stop = ".";    // user enters this to quit
                                               // null disables this feature

    // Methods ---------------------------------------------------------

    /**
     * Run test (version with all options)
     * @param mtu the physical layer's maximum transfer unit (frame length)
     * @param revealControlCodes true if control codes are to be made visible
     * @param quiet true for quiet mode which suppresses prompts and status info
     */

    public static void runTest(int mtu, boolean revealControlCodes, boolean quiet)
    {
        // Announce start of test
        // Create physical layer frame receiver
        // Create data link layer message receiver

        if (!quiet) {
            System.out.println("TestReceiver started");
        }
        FrameReceiver physicalLayer = new FrameReceiver(mtu, quiet, stop);
        MessageReceiver dataLinkLayer = new MessageReceiver(physicalLayer, quiet);

        // Message processing loop
        // Give instructions on how to stop the test
        // Use System.err rather than System.out so instructions will
        // appear on screen even if user redirects output to a file
        // Loop repeats until end of input stream reached

        if (!quiet && stop != null) {
            System.err.println("Enter \"" + stop + "\" to stop test");
        }
        while (true) {

            // Invoke data link message receiver
            // Trap and report any exception, but don't terminate program

            String message = null;
            try {
                message = dataLinkLayer.receiveMessage();
            }
            catch (Exception e) {
                System.out.println("!!! Receive aborted due to error : " + e.getMessage());
                continue;
            }

            // Break out of loop if end of stream reached

            if (message == null)
                break;

            // Print message

            if (!quiet) {
                System.out.print("Message received => ");
            }
            if (revealControlCodes) {
                printAndRevealControlCodes(message);  // reveal on
            }
            else {
                System.out.println(message);          // reveal off
            }

        } // end of message processing loop

        // Test ended normally

        if (!quiet) {
            System.out.println("TestReceiver finished");
        }

    } // end of method runTest

  // -------------------------------------------------------------------

    /**
     * Run test (version using sensible defaults for most options)
     * @param mtu the physical layer's maximum transfer unit (frame length)
     */

    public static void runTest(int mtu)
    {
        runTest(mtu, false, false);
    }

    /**
     * Run test (version using sensible defaults for all options)
     */

    public static void runTest()
    {
        runTest(defaultMtu, false, false);
    }

    // -----------------------------------------------------------------

  /**
   * Main method used when the program is executed from a command line.
   * DON'T USE THIS METHOD UNDER BLUEJ.
   * @param args the command line arguments
   * @throws IOException if unexpected i/o error occurs
   */

    public static void main(String[] args)
    {
        // Set options to default values

        int mtu = defaultMtu;               // maximum transfer unit (frame length limit)
        boolean revealControlCodes = false; // true=make control codes visible
        boolean quiet = false;              // true=quiet mode (suppress prompts and status info)

        // Parse command line options
        // Loop for each command line argument in turn

        for (int i = 0; i < args.length; i++) {

            // If quiet option found, set accordingly

            if (args[i].equalsIgnoreCase("-q")) {
                quiet = true;
            }

            // If MTU option found, decode the value
            // Abort program if bad

            else if (args[i].toLowerCase().startsWith("-m")) {
                try {
                    mtu = Integer.parseInt(args[i].substring(2).trim());
                }
                catch (Exception e) {
                  usageErrorExit("Bad or missing MTU value on command line");
                }
            }

            // If control code reveal option found, set accordingly

            else if (args[i].equalsIgnoreCase("-c")) {
                revealControlCodes = true;
            }

            // Abort program if unrecognised argument found

            else {
                usageErrorExit("Unrecognised command line option " + args[i]);
            }

        } // end for loop that parses command line args

        // Run test with options specified

        runTest(mtu, revealControlCodes, quiet);

    } // end of main method

    // -----------------------------------------------------------------

    /**
     * Handle program usage errors.
     * Forces exit.
     * @param errorMessage message describing error
     */

    private static void usageErrorExit(String errorMessage)
    {
        // Display error message
        // Give usage info
        // Force exit

        System.err.println("!!! Program aborted : " + errorMessage);
        System.err.println("Usage  : java TestReceiver options");
        System.err.println("Options: -mN  set mtu to N (defaults to " + defaultMtu + ")");
        System.err.println("         -c   reveal control codes");
        System.err.println("         -q   enable quiet mode");
        System.exit(1);
    }

    // -----------------------------------------------------------------

    /**
     * Print string and reveal any control codes (make them visible)
     */

    private static void printAndRevealControlCodes(String str)
    {
        // Process string one character at a time

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);            // extract next char
            if (Character.isISOControl(ch)) {   // test for control code
                System.out.print("(CONTROL)");  // print warning text if found
            }
            else {
                System.out.print(ch);           // else just print the char
            }
        }
        System.out.println();                   // Terminate line
    }

} // end of class TestReceiver

