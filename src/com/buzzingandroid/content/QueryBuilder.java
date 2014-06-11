package com.buzzingandroid.content;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.AsyncQueryHandler;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.FilterQueryProvider;

/**
 * A class designed to simplify selections in query, update and delete
 * operations through a {@link ContentProvider} 
 * @author Jesper Borgstrup
 *
 */
public class QueryBuilder {
	
	public QueryBuilder() {}
	
	private StringBuilder selection = new StringBuilder();
	private ArrayList<String> selectionArgs = new ArrayList<String>();
	
	private String[] searchColumns = new String[0];
	private String[] searchQueryTokens = new String[0];
	private String[] projection;
	private String sortOrder;
	
	/**
	 * Requires the ID column ({@link BaseColumns#_ID}) to be the specified long.<br />
	 * Similar to the SQL expression <tt>_id=[id]</tt>
	 * @param id 
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereId( long id ) {
		readySelection();
		selection.append( BaseColumns._ID ).append( "=?" );
		selectionArgs.add( Long.toString( id ) );
		return this;
	}
	
	/**
	 * Requires the ID column ({@link BaseColumns#_ID}) to be the specified integer.<br />
	 * Similar to the SQL expression <tt>_id=[id]</tt>
	 * @param id 
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereId( int id ) {
		readySelection();
		selection.append( BaseColumns._ID ).append( "=?" );
		selectionArgs.add( Integer.toString( id ) );
		return this;
	}
	
	/**
	 * Requires the specified column to be null.<br />
	 * Similar to the SQL expression <tt>[column] IS NULL</tt>
	 * @param column
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnIsNull( String column ) {
		readySelection();
		selection.append( column ).append( " IS NULL" );
		return this;
	}
	
	/**
	 * Requires the specified column to not be null.<br />
	 * Similar to the SQL expression <tt>[column] IS NOT NULL</tt>
	 * @param column
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnIsNotNull( String column ) {
		readySelection();
		selection.append( column ).append( " IS NOT NULL" );
		return this;
	}
	
	/**
	 * Requires the specified column to be the specified value.<br />
	 * Similar to the SQL expression <tt>[column]=[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnEquals( String column, Object value ) {
		readySelection();
		selection.append(column).append(value == null ? " IS ?" : "=?");
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to not be the specified value.<br />
	 * Similar to the SQL expression <tt>[column]!=[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnNotEquals( String column, Object value ) {
		readySelection();
		selection.append(column).append(value == null ? " IS NOT ?" : "!=?");
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to be greater than the specified value.<br />
	 * Similar to the SQL expression <tt>[column]>[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnGreaterThan( String column, Object value ) {
		readySelection();
		selection.append( column ).append( ">?" );
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to be greater than or equal to the specified value.<br />
	 * Similar to the SQL expression <tt>[column]>=[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnGreaterThanOrEqual( String column, Object value ) {
		readySelection();
		selection.append( column ).append( ">=?" );
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to be less than the specified value.<br />
	 * Similar to the SQL expression <tt>[column]<[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnLessThan( String column, Object value ) {
		readySelection();
		selection.append( column ).append( "<?" );
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to be less than or equal to the specified value.<br />
	 * Similar to the SQL expression <tt>[column]<=[value]</tt>
	 * @param column
	 * @param value
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnLessThanOrEqual( String column, Object value ) {
		readySelection();
		selection.append( column ).append( "<=?" );
		selectionArgs.add( value == null ? null : value.toString() );
		return this;
	}
	
	/**
	 * Requires the specified column to be one of the specified values.<br />
	 * Similar to the SQL expression <tt>[column] IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnInSet( String column, Object[] set ) {
		readySelection();
		selection.append( column ).append( " IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Requires the specified column to be one of the specified longs.<br />
	 * Similar to the SQL expression <tt>[column] IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnInSet( String column, long[] set ) {
		readySelection();
		selection.append( column ).append( " IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Requires the specified column to be one of the specified integers.<br />
	 * Similar to the SQL expression <tt>[column] IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnInSet( String column, int[] set ) {
		readySelection();
		selection.append( column ).append( " IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Requires the specified column to not be one of the specified values.<br />
	 * Similar to the SQL expression <tt>[column] NOT IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnNotInSet( String column, Object[] set ) {
		readySelection();
		selection.append( column ).append( " NOT IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Requires the specified column to not be one of the specified longs.<br />
	 * Similar to the SQL expression <tt>[column] NOT IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnNotInSet( String column, long[] set ) {
		readySelection();
		selection.append( column ).append( " NOT IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Requires the specified column to not be one of the specified integers.<br />
	 * Similar to the SQL expression <tt>[column] NOT IN ([set])</tt>
	 * @param column
	 * @param set
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder whereColumnNotInSet( String column, int[] set ) {
		readySelection();
		selection.append( column ).append( " NOT IN (" );
		joinInSelection( set );
		selection.append( ")" );
		return this;
	}
	
	/**
	 * Adds an extra SQL selection string to the where-clause
	 * @param extraSelection The selection string
	 * @param extraSelectionArgs One argument for every question mark in the selection string
	 * @return The QueryBuilder instance for chaining method calls
	 */
	public QueryBuilder addSelection( String extraSelection, Object... extraSelectionArgs ) {
		readySelection();
		selection.append( extraSelection );
		for ( Object arg: extraSelectionArgs ) {
			selectionArgs.add( arg == null ? null : arg.toString() );
		}
		return this;
	}
	
	/**
	 * Sets one or more columns for free-text searching.<br />
	 * Note that subsequent calls to this method will overwrite previously set search columns.
	 * @param columns
	 * @return
	 */
	public QueryBuilder setSearchColumns( String... columns ) {
		this.searchColumns = columns;
		return this;
	}
	
	/**
	 * Sets a query to be searched for in the column(s) defined in {@link #setSearchColumns(String...)}.<br /><br />
	 * 
	 * The query is broken into tokens separated by whitespace, and each of these tokens has to be occur in at least
	 * one of the search columns in order for a row to be selected.<br />
	 * <br />
	 * Note that subsequent calls to this method will replace any search query set earlier.
	 * @param query
	 * @return
	 */
	public QueryBuilder setSearchQuery( String query ) {
		if ( TextUtils.isEmpty( query ) ) {
			
			this.searchQueryTokens = new String[ 0 ];
			
		} else {
		
			StringTokenizer tokenizer = new StringTokenizer( query );
			this.searchQueryTokens = new String[ tokenizer.countTokens() ];
			for ( int index = 0; tokenizer.hasMoreTokens(); index++ ) {
				this.searchQueryTokens[ index ] = tokenizer.nextToken();
			}
			
		}
		return this;
	}
	
	/**
	 * Sets the column projection to be returned when querying.<br />
	 * <br />
	 * You must call this method with a non-zero amount of columns before doing any querying.
	 * Failure to do so will result in an IllegalStateException being thrown.<br />
	 * <br />
	 * Note that subsequent calls to this method will overwrite previously selected columns.
	 * @param columns
	 * @return
	 */
	public QueryBuilder select( String... columns ) {
		this.projection = columns;
		return this;
	}
	
	/**
	 * Sets the row sort order.<br />
	 * <br />
	 * This will be put after the ORDER BY keywords in the SQL expression,
	 * e.g. <tt>SELECT * FROM [table] ORDER BY [sortOrder]</tt>
	 * @param sortOrder
	 * @return
	 */
	public QueryBuilder orderBy( String sortOrder ) {
		this.sortOrder = sortOrder;
		return this;
	}

	/**
	 * Builds a selection string and an array of arguments to be used directly in
	 * an query, update or delete call
	 * @return First value of the pair is the selection string, second value is the arguments (both may be null).
	 * The pair itself will never be null.
	 */
	public Pair<String, String[]> buildSelection() {
		
		/*
		 * First build the selection string
		 */
		StringBuilder sb = new StringBuilder();
		sb.append( selection );
		if ( searchQueryTokens.length > 0 && searchColumns.length > 0 ) {
			readySelection();
			/*
			 * Build single search selection
			 */
			StringBuilder sb2 = new StringBuilder();
			for ( int i = 0; i < searchColumns.length; i++ ) {
				sb2.append( searchColumns[i] );
				sb2.append( " LIKE ?" );
				if ( i < searchColumns.length-1 ) { sb2.append( " OR " ); }
			}
			
			/*
			 * Build search selection for all tokens
			 */
			final String singleSearchSelection = sb2.toString();
			sb2 = new StringBuilder();
			for ( int i = 0; i < searchQueryTokens.length; i++ ) {
				sb2.append( "(" );
				sb2.append( singleSearchSelection );
				sb2.append( ")" );
				if ( i < searchQueryTokens.length-1 ) { sb2.append( " AND " ); }
			}
			
			sb.append( sb2 );
		}
		
		String selectionString = sb.length() == 0 ? null : sb.toString();
		
		/*
		 * Second, build the selection argument array
		 */
		ArrayList<String> argList = new ArrayList<String>( selectionArgs );
		if ( searchQueryTokens.length > 0 && searchColumns.length > 0 ) {
			for ( int i = 0; i < searchQueryTokens.length; i++ ) {
				String arg = String.format( "%%%s%%", searchQueryTokens[i] );
				for ( int j = 0; j < searchColumns.length; j++ ) {
					argList.add( arg );
				}
			}
		}
		
		String[] selectionArgsArray = argList.toArray( new String[ argList.size() ] );
		
		return new Pair<String, String[]>( selectionString, selectionArgsArray );
	}
	
	/**
	 * Ensure that we can query, by checking that at least one column is selected for projection.
	 */
	private void validateForQuery() {
		if ( projection == null || projection.length == 0 ) {
			throw new IllegalStateException( "No projection defined. Set one with select(String...)" );
		}
	}
	
	/**
	 * Queries the defined projection, selection and sort order on the given URI through the ContentResolver
	 * retrieved from the given context
	 * @param context
	 * @param uri
	 * @return
	 */
	public Cursor query( Context context, Uri uri ) {
		validateForQuery();
		Pair<String, String[]> builtSelection = buildSelection();
		return context.getContentResolver().query( uri,
												   projection,
												   builtSelection.first,
												   builtSelection.second,
												   sortOrder );
	}
	/**
	 * Queries the defined projection, selection and sort order on the given URI through the ContentProviderClient
	 * @param provider
	 * @param uri
	 * @return
	 * @throws RemoteException May be thrown from {@link ContentProviderClient#query(Uri, String[], String, String[], String)}
	 */
	public Cursor query( ContentProviderClient provider, Uri uri ) throws RemoteException {
		validateForQuery();
		Pair<String, String[]> builtSelection = buildSelection();
		return provider.query( uri,
							   projection,
							   builtSelection.first,
							   builtSelection.second,
							   sortOrder );
	}
	public interface AsyncQueryCallback {
		public void queryCompleted( Cursor c );
	}
	/**
	 * Queries the defined projection, selection and sort order on a background thread
	 * on the given URI through the ContentResolver retrieved from the given context.<br />
	 * <br />
	 * The callback is called on the thread that called this method when the query finishes.
	 * @param context
	 * @param uri
	 * @param callback
	 */
	public void queryAsync( Context context, Uri uri, final AsyncQueryCallback callback ) {
		validateForQuery();
		AsyncQueryHandler aqh = new AsyncQueryHandler( context.getContentResolver() ) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				callback.queryCompleted(cursor);
				if ( !cursor.isClosed() ) {
					cursor.close();
				}
			}
			
		};
		
		Pair<String, String[]> builtSelection = buildSelection();
		aqh.startQuery(0,
					   null,
					   uri,
					   projection,
					   builtSelection.first,
					   builtSelection.second,
					   sortOrder );
	}
	
	/**
	 * Creates a CursorLoader that queries the defined projection, selection and sort order
	 * with the specified uri.
	 * @param context
	 * @param uri
	 * @return
	 */
	public CursorLoader createCursorLoader( Context context, Uri uri ) {
		validateForQuery();
		Pair<String, String[]> builtSelection = buildSelection();
		return new CursorLoader(context,
								uri,
								projection,
								   builtSelection.first,
								   builtSelection.second,
								sortOrder );
	}
	
	/**
	 * Creates a SearchFilterProvider that searches through the columns set in {@link #setSearchColumns(String...)}.
	 * @param context
	 * @return
	 */
	public FilterQueryProvider createSearchFilterQueryProvider( final Context context, final Uri uri ) {
		return new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				setSearchQuery( TextUtils.isEmpty( constraint ) ? null : constraint.toString() );
				return query( context, uri );
			}
		};
	}
	
	/**
	 * Updates any row that matches the defined selection, with the specified values.
	 * @param context
	 * @param values
	 * @param uri
	 * @return
	 */
	public int update( Context context, ContentValues values, Uri uri ) {
		Pair<String, String[]> builtSelection = buildSelection();
		return context.getContentResolver().update( uri,
													values, 
													builtSelection.first,
													builtSelection.second );
	}
	
	/**
	 * Updates any row that matches the defined selection, with the specified values.
	 * @param provider
	 * @param values
	 * @param uri
	 * @return
	 * @throws RemoteException May be thrown from {@link ContentProviderClient#update(Uri, ContentValues, String, String[])}
	 */
	public int update( ContentProviderClient provider, ContentValues values, Uri uri ) throws RemoteException {
		Pair<String, String[]> builtSelection = buildSelection();
		return provider.update( uri,
								values, 
								builtSelection.first,
								builtSelection.second );
	}
	
	/**
	 * Creates an update operation that updates any row that matches the defined selection, with the specified values.<br/>
	 * <br/>
	 * To be used in batch operations with {@link ContentProvider#applyBatch(ArrayList)}.
	 * @return
	 */
	public ContentProviderOperation createUpdateOperation( ContentValues values, Uri uri) {
		Pair<String, String[]> builtSelection = buildSelection();
		return ContentProviderOperation.newUpdate( uri ).withSelection( builtSelection.first, builtSelection.second ).withValues( values ).build();
	}
	
	/**
	 * Deletes any row that matches the defined selection.
	 * @param context
	 * @param uri
	 * @return
	 */
	public int delete( Context context, Uri uri ) {
		Pair<String, String[]> builtSelection = buildSelection();
		return context.getContentResolver().delete( uri,
													builtSelection.first,
													builtSelection.second );
	}
	
	/**
	 * Deletes any row that matches the defined selection.
	 * @param provider
	 * @param uri
	 * @return
	 * @throws RemoteException
	 */
	public int delete( ContentProviderClient provider, Uri uri ) throws RemoteException {
		Pair<String, String[]> builtSelection = buildSelection();
		return provider.delete( uri,
								builtSelection.first,
								builtSelection.second );
	}
	
	/**
	 * Creates a delete operation that deletes any row that matches the defined selection.<br/>
	 * <br/>
	 * To be used in batch operations with {@link ContentProvider#applyBatch(ArrayList)}.
	 * @param uri
	 * @return
	 */
	public ContentProviderOperation createDeleteOperation( Uri uri ) {
		Pair<String, String[]> builtSelection = buildSelection();
		return ContentProviderOperation.newDelete( uri ).withSelection( builtSelection.first, builtSelection.second ).build();
	}

	/**
	 * Make ready for another expression by appending "<tt> AND </tt>" if the selection
	 * is non-empty
	 */
	private void readySelection() {
		if ( selection.length() > 0 ) {
			selection.append( " AND " );
		}
	}
	
	/**
	 * Append the objects in the array to the selection, each separated by a comma
	 * @param objects
	 */
	private void joinInSelection( Object[] objects ) {
		for ( int i = 0; i < objects.length; i++ ) {
			selection.append( objects[i] );
			if ( i != objects.length - 1 ) {
				selection.append( ',' );
			}
		}
	}
	/**
	 * Append the longs in the array to the selection, each separated by a comma
	 * @param objects
	 */
	private void joinInSelection( long[] objects ) {
		for ( int i = 0; i < objects.length; i++ ) {
			selection.append( objects[i] );
			if ( i != objects.length - 1 ) {
				selection.append( ',' );
			}
		}
	}
	/**
	 * Append the integers in the array to the selection, each separated by a comma
	 * @param objects
	 */
	private void joinInSelection( int[] objects ) {
		for ( int i = 0; i < objects.length; i++ ) {
			selection.append( objects[i] );
			if ( i != objects.length - 1 ) {
				selection.append( ',' );
			}
		}
	}
}