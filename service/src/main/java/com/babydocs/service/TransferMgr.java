package com.babydocs.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.transfer.*;
import exceptions.S3UploadException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class TransferMgr {
    public static void waitForCompletion(Transfer xfer) {

        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            log.info("Amazon service error: " + e.getErrorCode() + "." + e.getErrorMessage());
            throw new S3UploadException("Amazon service error: " + e.getErrorCode() + "." + e.getErrorMessage());
        } catch (AmazonClientException e) {
            log.info("Amazon client error: " + e.getMessage());
            throw new S3UploadException("Amazon client error: " + e.getMessage());
        } catch (InterruptedException e) {
            log.info("Transfer interrupted: " + e.getMessage());
            throw new S3UploadException("Transfer interrupted " + e.getMessage());
        } finally {
           // tm.shutdownNow();
        }
    }

    // Prints progress while waiting for the transfer to finish.
    public static void showTransferProgress(Transfer xfer) {
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (!xfer.isDone());
        // print the final state of the transfer.
        Transfer.TransferState xfer_state = xfer.getState();
        System.out.println(": " + xfer_state);
    }

    // Prints progress of a multiple file upload while waiting for it to finish.
    public static void showMultiUploadProgress(MultipleFileUpload multi_upload) {
        // print the upload's human-readable description
        System.out.println(multi_upload.getDescription());

        Collection<? extends Upload> sub_xfers = new ArrayList<Upload>();
        sub_xfers = multi_upload.getSubTransfers();

        do {
            System.out.println("\nSubtransfer progress:\n");
            for (Upload u : sub_xfers) {
                System.out.println("  " + u.getDescription());
                if (u.isDone()) {
                    Transfer.TransferState xfer_state = u.getState();
                    System.out.println("  " + xfer_state);
                } else {
                    TransferProgress progress = u.getProgress();
                    double pct = progress.getPercentTransferred();
                    printProgressBar(pct);
                    System.out.println();
                }
            }

            // wait a bit before the next update.
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return;
            }
        } while (!multi_upload.isDone());
        // print the final state of the transfer.
        Transfer.TransferState xfer_state = multi_upload.getState();
        System.out.println("\nMultipleFileUpload " + xfer_state);
    }

    // prints a simple text progressbar: [#####     ]
    public static void printProgressBar(double pct) {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
                empty_bar.substring(0, bar_size - amt_full));
    }

    // erases the progress bar.
    public static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }


}
