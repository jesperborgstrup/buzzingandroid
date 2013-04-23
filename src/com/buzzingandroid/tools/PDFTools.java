package com.buzzingandroid.tools;

import java.io.File;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.buzzingandroid.R;

public class PDFTools {
	private static final String GOOGLE_DRIVE_PDF_READER_PREFIX = "http://drive.google.com/viewer?url=";
	private static final String PDF_MIME_TYPE = "application/pdf";
	private static final String HTML_MIME_TYPE = "text/html";
	
	/**
	 * If a PDF reader is installed, download the PDF file and open it in a reader. 
	 * Otherwise ask the user if he/she wants to view it in the Google Drive online PDF reader.<br />
	 * <br />
	 * <b>BEWARE:</b> This method
	 * @param context
	 * @param pdfUrl
	 * @return
	 */
	public static void showPDFUrl( final Context context, final String pdfUrl ) {
		if ( isPDFSupported( context ) ) {
			downloadAndOpenPDF(context, pdfUrl);
		} else {
			askToOpenPDFThroughGoogleDrive( context, pdfUrl );
		}
	}

	/**
	 * Downloads a PDF with the Android DownloadManager and opens it with an installed PDF reader app.
	 * @param context
	 * @param pdfUrl
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void downloadAndOpenPDF(final Context context, final String pdfUrl) {
		// Get filename
		final String filename = pdfUrl.substring( pdfUrl.lastIndexOf( "/" ) + 1 );
		// The place where the downloaded PDF file will be put
		final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), filename );
		if ( tempFile.exists() ) {
			// If we have downloaded the file before, just go ahead and show it.
			openPDF( context, Uri.fromFile( tempFile ) );
			return;
		}

		// Show progress dialog while downloading
		final ProgressDialog progress = ProgressDialog.show( context, context.getString( R.string.pdf_show_local_progress_title ), context.getString( R.string.pdf_show_local_progress_content ), true );
		
		// Create the download request
		DownloadManager.Request r = new DownloadManager.Request( Uri.parse( pdfUrl ) );
		r.setDestinationInExternalFilesDir( context, Environment.DIRECTORY_DOWNLOADS, filename );
		final DownloadManager dm = (DownloadManager) context.getSystemService( Context.DOWNLOAD_SERVICE );
		BroadcastReceiver onComplete = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if ( !progress.isShowing() ) {
					return;
				}
				context.unregisterReceiver( this );
				
				progress.dismiss();
				long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, -1 );
				Cursor c = dm.query( new DownloadManager.Query().setFilterById( downloadId ) );
				
				if ( c.moveToFirst() ) {
					int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
					if ( status == DownloadManager.STATUS_SUCCESSFUL ) {
						openPDF( context, Uri.fromFile( tempFile ) );
					}
				}
				c.close();
			}
		};
		context.registerReceiver( onComplete, new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE ) );
		
		// Enqueue the request
		dm.enqueue( r );
	}
	
	/**
	 * Show a dialog asking the user if he wants to open the PDF through Google Drive
	 * @param context
	 * @param pdfUrl
	 */
	public static void askToOpenPDFThroughGoogleDrive( final Context context, final String pdfUrl ) {
		new AlertDialog.Builder( context )
			.setTitle( R.string.pdf_show_online_dialog_title )
			.setMessage( R.string.pdf_show_online_dialog_question )
			.setNegativeButton( R.string.pdf_show_online_dialog_button_no, null )
			.setPositiveButton( R.string.pdf_show_online_dialog_button_yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					openPDFThroughGoogleDrive(context, pdfUrl);	
				}
			})
			.show();
	}
	
	/**
	 * Launches a browser to view the PDF through Google Drive
	 * @param context
	 * @param pdfUrl
	 */
	public static void openPDFThroughGoogleDrive(final Context context, final String pdfUrl) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType(Uri.parse(GOOGLE_DRIVE_PDF_READER_PREFIX + pdfUrl ), HTML_MIME_TYPE );
		context.startActivity( i );
	}
	/**
	 * Open a local PDF file with an installed reader
	 * @param context
	 * @param localUri
	 */
	public static final void openPDF(Context context, Uri localUri ) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType( localUri, PDF_MIME_TYPE );
		context.startActivity( i );
	}
	/**
	 * Checks if any apps are installed that supports reading of PDF files.
	 * @param context
	 * @return
	 */
	public static boolean isPDFSupported( Context context ) {
		Intent i = new Intent( Intent.ACTION_VIEW );
		final File tempFile = new File( context.getExternalFilesDir( Environment.DIRECTORY_DOWNLOADS ), "test.pdf" );
		i.setDataAndType( Uri.fromFile( tempFile ), PDF_MIME_TYPE );
		return context.getPackageManager().queryIntentActivities( i, PackageManager.MATCH_DEFAULT_ONLY ).size() > 0;
	}

}
