// MessageSender.java (partial implementation)
import java.util.*;
import java.lang.*;

/**
 * This class implements the sender side of the data link layer.
 * <P>
 * The source code supplied here contains only a partial implementation. 
 * Your completed version must be submitted for assessment.
 * <P>
 * You only need to finish the implementation of the sendMessage
 * method to complete this class.  No other parts of this file need to
 * be changed.  Do NOT alter the constructor or interface of any public
 * method.  Do NOT put this class inside a package.  You may add new
 * private methods, if you wish, but do NOT create any new classes. 
 * Only this file will be processed when your work is marked.
 */

public class MessageSender
{
    // Fields ----------------------------------------------------------

    private FrameSender physicalLayer;   // physical layer object
    private boolean quiet;               // true=quiet mode (suppress
    // prompts and status info)

    // You may add additional fields but this shouldn't be necessary

    // Constructor -----------------------------------------------------

    /**
     * MessageSender constructor (DO NOT ALTER ANY PART OF THIS)
     * Create and initialize new MessageSender.
     * @param physicalLayer physical layer object with frame sender service
     * (this will already have been created and initialized by TestSender)
     * @param quiet true for quiet mode which suppresses prompts and status info
     */

    public MessageSender(FrameSender physicalLayer, boolean quiet)
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
     * Send a message (THIS IS THE ONLY METHOD YOU NEED TO MODIFY)
     * @param message the message to be sent.  The message can be any
     * length and may be empty but the string reference should not
     * be null.
     * @throws ProtocolException immediately without attempting to
     * send any further frames if (and only if) the physical layer
     * throws an exception or the given message can't be sent
     * without breaking the rules of the protocol (e.g. if the MTU
     * is too small)
     */

    public void sendMessage(String message) throws ProtocolException
    {
        // Announce action
        // (Not required by protocol but helps when debugging)

        //Calculate space left for message in frame. MTU has to be set to greater than 10 to allow for control
        //int frameSpaceLeft = physicalLayer.getMTU()-10;
        if(physicalLayer.getMTU()<=10)
        {
            throw new ProtocolException("Must have an MTU value which is greater than 10");
        }
        else
        {
            int frameSpaceLeft = physicalLayer.getMTU()-10;

            if (!quiet) {
                System.out.println("Sending message => " + message);
            }

            //create checksum value
            int checkSum = 0;
            //create segment length
            int segmentLength = 0;

            //create string to store checksum
            String checkSumFormatted = "";
            //create string to store segment legth
            String segmentLengthFormatted = ""; 

            //string status to show if last frame or not.
            //E = last frame
            //D = Not last frame
            String isLastFrame = "D";

            //Store split messages into an array
            ArrayList<String> messageArray = new ArrayList<String>();

            String frame = "";

            //check if message is bigger then frame length. If bigger split into required length
            if(message.length()>frameSpaceLeft)
            {
                for(int i = 0;i<message.length();)
                {
                    frame = "";
                    for(int j = 0; (j<frameSpaceLeft) && (i<message.length()); j++)
                    {
                        char ch = message.charAt(i);
                        //if there is still space within frame then add character to frame
                        if(j<frameSpaceLeft)
                        {
                            frame = frame + ch;
                            i++;
                        }
                    }
                    messageArray.add(frame);
                }
            }
            else 
            {
                frame = message;
                messageArray.add(frame);
            }

            

            //loop through array, calculating segmentLength and checkSum for each frame
            for(int i = 0; i<messageArray.size(); i++)
            {

                frame = messageArray.get(i);
                //set segment length
                segmentLength = frame.length();

                //check if frame is last in array
                if(i == (messageArray.size()-1))
                {
                    isLastFrame = "E";
                }

                //format segment length to 2 digit decimal
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

                //calculate checksum value for the message.
                if (frame.equals(""))
                {
                    checkSum = 0;
                }
                else
                {
                    int temp = 0;
                    for(int j = 0; j< frame.length(); j++)
                    {
                        temp = temp + frame.charAt(j);
                    }
                    checkSum = temp + '-' + '-' + '-' + isLastFrame.charAt(0) + segmentLengthFormatted.charAt(0) + segmentLengthFormatted.charAt(1);  
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

                //Padding
                //Pad checkSum with appropriate number of zeros. numbers 10 - 99 = dont pad
                if(checkSum>9 && checkSum<100)
                {checkSumFormatted = Integer.toString(checkSum);}
                //Pad checkSum with appropriate number of zeros. numbers 0 = pad two zeros
                if(checkSum==0)
                {checkSumFormatted = "00";}
                //Pad checkSum with appropriate number of zeros. numbers 1 - 9 = pad one zeros
                if(checkSum>0 && checkSum<10)
                {checkSumFormatted = "0" + checkSum;}

                //if message string is empty send frame without message
                if(frame.equals(""))
                {physicalLayer.sendFrame("<" + isLastFrame + "-" + segmentLengthFormatted + "-" + "-" + checkSumFormatted + ">");}
                //else send with message
                else
                {physicalLayer.sendFrame("<" + isLastFrame + "-" + segmentLengthFormatted + "-" +  frame + "-" + checkSumFormatted + ">");}
            }

            // The following statement shows how the frame sender is invoked.
            // At the moment it just passes the whole message directly to
            // the physical layer.  That is, of course, incorrect!

            //physicalLayer.sendFrame(message);

            // sendMessage should split large messages into several smaller
            // segments.  Each segment must be encoded as a frame in the
            // format specified.  sendFrame will need to be called separately
            // for each frame in turn.  See the coursework specification
            // and other class documentation for further info.

        }

    }
}
// end of method sendMessage

// You may add private methods if you wish
// end of class MessageSender
