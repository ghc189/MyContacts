package com.guo.MyContacts;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ContactsProvider extends ContentProvider
{
	//��ǩ
	private static final String TAG= "ContactsProvider"; 
	//���ݿ������
	private DBHelper dbHelper;
	//���ݿ�
	private SQLiteDatabase contactsDB;
	//���ݿ����uri��ַ
	public static final String AUTHORITY = "com.guo.provider.ContactsProvider";
	public static final String CONTACTS_TABLE = "contacts";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+CONTACTS_TABLE);
	
	//�������Զ��������
	public static final int CONTACTS = 1;
	public static final int CONTACT_ID = 2;
	private static final UriMatcher uriMatcher;	
	static
	{
		//û��ƥ�����Ϣ
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//ȫ����ϵ����Ϣ
		uriMatcher.addURI(AUTHORITY,"contacts",CONTACTS);
		//����һ����ϵ����Ϣ
		uriMatcher.addURI(AUTHORITY,"contacts/#",CONTACT_ID);
	}
	//ȡ�����ݿ�
	@Override
	public boolean onCreate()
	{
		dbHelper = new DBHelper(getContext());
		//ִ�д������ݿ�
		contactsDB = dbHelper.getWritableDatabase();
		return (contactsDB == null) ? false : true;
	}
	
	// ɾ��ָ��������
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs)
	{
		int count;
		switch (uriMatcher.match(uri))
		{
			//ɾ����������where����
			case CONTACTS:
				count = contactsDB.delete(CONTACTS_TABLE, where, selectionArgs);
				break;
			case CONTACT_ID:
				//ȡ����ϵ�˵�id��Ϣ
				String contactID = uri.getPathSegments().get(1);
				//ɾ������where����������idֵΪcontactID�ļ�¼
				count = contactsDB.delete(CONTACTS_TABLE, 
										  ContactColumn._ID 
										  + "=" + contactID 
										  + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""),
										  selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	// URI����ת��
	public String getType(Uri uri)
	{
		switch (uriMatcher.match(uri))
		{
			//������ϵ��
			case CONTACTS:
				return "vnd.android.cursor.dir/vnd.guo.android.mycontacts";
			//ָ����ϵ��
			case CONTACT_ID:
				return "vnd.android.cursor.item/vnd.guo.android.mycontacts";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	// ��������
	public Uri insert(Uri uri, ContentValues initialValues)
	{
		//�ж�uri��ַ�Ƿ�Ϸ�
		if (uriMatcher.match(uri) != CONTACTS)
		{
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		ContentValues values;
		if (initialValues != null)
		{
			values = new ContentValues(initialValues);
			Log.e(TAG + "insert", "initialValues is not null");
		}
		else
		{
			values = new ContentValues();
		}
		// �����Ӧ������û��ֵ��������Ĭ��ֵΪ����
		if (values.containsKey(ContactColumn.NAME) == false)
		{
			values.put(ContactColumn.NAME, "");
		}
		if (values.containsKey(ContactColumn.MOBILENUM) == false)
		{
			values.put(ContactColumn.MOBILENUM, "");
		}
		if (values.containsKey(ContactColumn.HOMENUM) == false)
		{
			values.put(ContactColumn.HOMENUM, "");
		}
		if (values.containsKey(ContactColumn.ADDRESS) == false)
		{
			values.put(ContactColumn.ADDRESS, "");
		}
		if (values.containsKey(ContactColumn.EMAIL) == false)
		{
			values.put(ContactColumn.EMAIL, "");
		}
		Log.e(TAG + "insert", values.toString());
		//��������
		long rowId = contactsDB.insert(CONTACTS_TABLE, null, values);
		if (rowId > 0)
		{
			//��idֵ����uri��ַ��
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			//֪ͨ�ı�
			getContext().getContentResolver().notifyChange(noteUri, null);
			Log.e(TAG + "insert", noteUri.toString());
			return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}
	
	// ��ѯ����
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Log.e(TAG + ":query", " in Query");
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		//����Ҫ��ѯ�����ݱ�
		qb.setTables(CONTACTS_TABLE);

		switch (uriMatcher.match(uri))
		{
			//����where��䣬��λ��ָ��idֵ����
			case CONTACT_ID:
				qb.appendWhere(ContactColumn._ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}
		//��ѯ
		Cursor c = qb.query(contactsDB, projection, selection, selectionArgs, null, null, sortOrder);
		//����֪ͨ�ı��uri
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	// �������ݿ�
	public int update(Uri uri, ContentValues values, String where, String[] selectionArgs)
	{
		int count;
		Log.e(TAG + "update", values.toString());
		Log.e(TAG + "update", uri.toString());
		Log.e(TAG + "update :match", "" + uriMatcher.match(uri));
		switch (uriMatcher.match(uri))
		{
			//����where�����������и���
			case CONTACTS:
				Log.e(TAG + "update", CONTACTS + "");
				count = contactsDB.update(CONTACTS_TABLE, values, where, selectionArgs);
				break;
			//����ָ����
			case CONTACT_ID:
				String contactID = uri.getPathSegments().get(1);
				Log.e(TAG + "update", contactID + "");
				count = contactsDB.update(CONTACTS_TABLE, values, ContactColumn._ID + "=" + contactID
						+ (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		//֪ͨ����
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}