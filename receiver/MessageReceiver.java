// MessageReceiver.java (partial implementation)
import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * This class implements the receiver side of the data link layer.
 * <P>
 * The source code supplied here contains only a partial implementation.
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the receiveMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 */

public class MessageReceiver
{
    // Fields ----------------------------------------------------------

    private FrameReceiver physicalLayer; // physical layer object
    private boolean quiet;               // true=quiet mode (suppress
    // prompts and status info)

    // You may add additional fields but this shouldn't be necessary
    boolean isStillReceiving = true;

    // Constructor -----------------------------------------------------

    /**
     * MessageReceiver constructor (DO NOT ALTER ANY PART OF THIS)
     * Create and initialize new MessageReceiver.
     * @param physicalLayer physical layer object with frame receiver service
     * (this will already have been created and initialized by TestReceiver)
     * @param quiet true for quiet mode which suppresses prompts and status info
     */

    public MessageReceiver(FrameReceiver physicalLayer, boolean quiet)
    {
        // Initialize fields and report status

        this.physicalLayer = physicalLayer;
        this.quiet = quiet;
        if (!quiet) {
            System.out.println("Data link layer ready");
        }
    }

    // Methods ---------------------------------------------------------

    /**
     * Receive a message (THIS IS THE ONLY METHOD YOU NEED TO MODIFY)
     * @return the message received, or null if the end of the input
     * stream has been reached.  (See receiveFrame documentation for
     * further explanation of how the end of the input stream is
     * detected and handled.)
     * @throws ProtocolException immediately without attempting to
     * receive any further frames if any error is detected, such as
     * a corrupt frame being received
     */

    public String receiveMessage() throws ProtocolException
    {
        // The following block of statements shows how the frame receiver
        // is invoked.  At the moment it just returns the frame directly
        // to the application layer.  That is, of course, incorrect!

        // receiveMessage should invoke receiveFrame one or more times
        // to obtain all of the frames that make up a message, extract
        // the message segments and join them together to recreate the
        // original message string.  The whole message is processed by
        // a single execution of receiveMessage.  See the coursework
        // specification and other class documentation for further info. 
        // Note the System.out.println statement isn't required by the
        // protocol, it's there just to help when debugging

        
        //if (!quiet)
        //{
        //    System.out.println("Frame received   => " + frame);
        // }

        String newMessageString = "";

        while(isStillReceiving)
        {
            String frame = physicalLayer.receiveFrame();
            if (!quiet)
            {
                System.out.println("Frame received   => " + frame);
            }
            // check if frame space 
            int frameSpaceLeft = physicalLayer.getMTU() - 10;

            if(frame.length()<10)
            {
                throw new ProtocolException("Frame is too small.");
            }

            //CHECK IF FRAME MATCHES REGEX
            Pattern regex = Pattern.compile("<([E/D])-([0-9][0-9])-(.*)-([0-9][0-9])>");
            Matcher regexMatcher = regex.matcher(frame);

            if(regexMatcher.find())
            {

            }
            else
            {
                throw new ProtocolException("Frame received is not in correct format");
            }

            //get required substrings from frame
            String oldMessageLength = frame.substring(3,5); //message length  ===  regexMatcher.group(2)//frame.substring(3,5)
            int lengthOfMessage = Integer.parseInt(oldMessageLength);  // message length stored as an int
            String oldMessage = frame.substring(6,6+lengthOfMessage); //message string  === regexMatcher.group(3)
            String oldCheckSum = regexMatcher.group(4); //message checkSum  ==== regexMatcher.group(4) // frame.substring(7+lengthOfMessage,9+lengthOfMessage)
            String oldIsLastFrame = frame.substring(1,2); //last message frame idntifier   === regexMatcher.group(1)

            //checkMessage (message);
            //create checksum value
            int checkSum = 0;
            //create segment length
            int segmentLength = 0;
            //create string to store checksum
            String checkSumFormatted = "";
            //create string to store segment legth
            String segmentLengthFormatted = ""; 

            //Store split messages into an array
            ArrayList<String> messageArray = new ArrayList<String>();

            //string status to show if last frame or not.
            //E = last frame
            //D = Not last frame
            String isLastFrame = "";

            //receive frame, calculate if segmentLength & checkSum is correct for each and store in arrayList

            //check how many frames stored in array list

            //checkMessageLength(messageLength);
            segmentLength = oldMessage.length();
            String segmentLengthFormated = "";

            int numberOfFrames = 0;

            if(segmentLength>99)
            {
                //reduce value to last two digits
                while(segmentLength>99)
                {
                    segmentLengthFormatted = Integer.toString(segmentLength);
                    segmentLengthFormatted = segmentLengthFormatted.substring(1);
                    segmentLength = Integer.valueOf(segmentLengthFormatted);
                }
            }

            //Padding
            //pad segment length with appropriate number of zeros. numbers 10 - 99 = don't pad
            if(segmentLength>9 && segmentLength<100)
            {segmentLengthFormatted = Integer.toString(segmentLength);}
            //Pad checkSum with appropriate number of zeros. numbers 0 = pad two zeros
            if(segmentLength==0)
            {segmentLengthFormatted = "00";}
            //pad segment length with appropriate number of zeros. numbers 1 - 9 = pad one zero
            if(segmentLength>0 && segmentLength<10)
            {segmentLengthFormatted = "0" + segmentLength;}

            if(oldMessageLength.equals(segmentLengthFormatted))
            {

            }
            else
            {
                throw new ProtocolException("the message was not valid, Recieved message length differs from that calculated.");
            }

            //String checkSum = frame.substring(7+messageLength,9+messageLength); //message checkSum
            ////checkCheckSum(checkSum);
            //check if frame matches regex pattern
            //if valid then check checkSum

            //calculate checksum value for the message.
            if (oldMessage.equals(""))
            {
                checkSum = 0;
            }
            else
            {
                int temp = 0;
                for(int j = 0; j< oldMessage.length(); j++)
                {
                    temp = temp + oldMessage.charAt(j);
                }
                checkSum = temp + '-' + '-' + '-' + oldIsLastFrame.charAt(0) + segmentLengthFormatted.charAt(0) + segmentLengthFormatted.charAt(1);  
            }

            //format checksum to 2 digit decimal
            if(checkSum>99)
            {
                //Reduce value to last three digits
                while(checkSum>99)
                {
                    checkSumFormatted = Integer.toString(checkSum);
                    checkSumFormatted = checkSumFormatted.substring(1);
                    checkSum = Integer.valueOf(checkSumFormatted);
                }
            }
            else if(checkSum == 0)
            {
                checkSumFormatted = Integer.toString(checkSum);
                checkSumFormatted = checkSumFormatted + "0";
            }

            //compare recieved checkSum to new created checkSum
            if(oldCheckSum.equals(checkSumFormatted))
            {

            }
            //else throw exception - frame does not match format
            else
            {
                throw new ProtocolException("the message was not valid,  Checksum calculated differs from that received.");
            }

            if(oldIsLastFrame.equals("E"))//to be changed
            {
                isStillReceiving = false;
            }

            newMessageString = newMessageString + oldMessage;
        }

        //checkIsLastFrame(isLastFrame);
        //
        //if(isLastFrame == "E")
        //{
        //    return true;//true if is last message
        //}
        //else if(isLastFrame == 'D')
        //{
        //    return false;
        //}
        //else
        //{
        //    throw new ProtocolException("the message '%s' was not valid: Did not receive correct last frame type. ");
        //}

        // if checks pass, reassamble frame and then send
        return newMessageString;
        //return frame;
    } // end of method receiveMessage
    // You may add private methods if you wish
} // end of class MessageReceiver
