package com.guo.MyContacts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ContactEditor extends Activity
{
	//��־λ���������ڱ�־��ǰ���½�״̬���Ǳ༭״̬
	private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;
    //
    private static final int REVERT_ID = Menu.FIRST;
    private static final int DISCARD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    
    private Cursor mCursor;
    private int mState;		//��ǰ�����½�״̬���Ǳ༭״̬�ı�־λ����
    private Uri mUri;
    //����Ԫ��
    private EditText nameText;
    private EditText mobileText;
    private EditText homeText;
    private EditText addressText;
    private EditText emailText;
    //����
    private Button okButton;
    private Button cancelButton;
    
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final String action = intent.getAction();
		//����action�Ĳ�ͬ���в�ͬ�Ĳ���
		//�༭��ϵ��
		if (Intent.ACTION_EDIT.equals(action))
		{
			mState = STATE_EDIT;
			mUri = intent.getData();
		}
		else if (Intent.ACTION_INSERT.equals(action))
		{
			//�������ϵ��
			mState = STATE_INSERT;
			mUri = getContentResolver().insert(intent.getData(), null);
			if (mUri == null)
			{
				finish();
				return;
			}
			setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
		}
		//����������˳�
		else
		{
			finish();
			return;
		}        
        setContentView(R.layout.editorcontacts);
        //��ʼ�������ı���
        nameText = (EditText) findViewById(R.id.EditText01);
        mobileText = (EditText) findViewById(R.id.EditText02);
        homeText = (EditText) findViewById(R.id.EditText03);
        addressText = (EditText) findViewById(R.id.EditText04);
        emailText = (EditText) findViewById(R.id.EditText05);
        //��ʼ������
        okButton = (Button)findViewById(R.id.Button01);
        cancelButton = (Button)findViewById(R.id.Button02);
        //����ȷ������������
        okButton.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				String text = nameText.getText().toString();
				if(text.length() == 0)
				{
					//���û�����붫������ԭ���ļ�¼ɾ��
					setResult(RESULT_CANCELED);
					deleteContact();
					finish();
				}
				else
				{
					//��������
					updateContact();
				}
			}       	
        });
        //����ȡ����ť������
        cancelButton.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				//����Ӽ�¼��Ҳ�������¼
				setResult(RESULT_CANCELED);
				deleteContact();
				finish();

			}
        });
        // ��ò�����ԭʼ��ϵ����Ϣ
        mCursor = managedQuery(mUri, ContactColumn.PROJECTION, null, null, null);
        mCursor.moveToFirst();
        if (mCursor != null)
		{
			// ��ȡ����ʾ��ϵ����Ϣ
			mCursor.moveToFirst();
			if (mState == STATE_EDIT)
			{
				setTitle(getText(R.string.editor_user));
			}
			else if (mState == STATE_INSERT)
			{
				setTitle(getText(R.string.add_user));
			}
			String name = mCursor.getString(ContactColumn.NAME_COLUMN);
			String moblie = mCursor.getString(ContactColumn.MOBILENUM_COLUMN);
			String home = mCursor.getString(ContactColumn.HOMENUM_COLUMN);
			String address = mCursor.getString(ContactColumn.ADDRESS_COLUMN);
			String email = mCursor.getString(ContactColumn.EMAIL_COLUMN);
			//��ʾ��Ϣ
			nameText.setText(name);
			mobileText.setText(moblie);
			homeText.setText(home);
			addressText.setText(address);
			emailText.setText(email);
		}
		else
		{
			setTitle("������Ϣ");
		}
	}		
    //�˵�ѡ��
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        if (mState == STATE_EDIT) 
        {
        	//���ذ�ť
            menu.add(0, REVERT_ID, 0, R.string.revert)
                    .setShortcut('0', 'r')
                    .setIcon(R.drawable.listuser);
            //ɾ����ϵ�˰�ť
            menu.add(0, DELETE_ID, 0, R.string.delete_user)
            .setShortcut('0', 'f')
            .setIcon(R.drawable.remove);
        } 
        else 
        {
        	//���ذ�ť
            menu.add(0, DISCARD_ID, 0, R.string.revert)
                    .setShortcut('0', 'd')
                    .setIcon(R.drawable.listuser);
        }
        return true;
    }
    //�˵�����
	@Override
    public boolean onOptionsItemSelected(MenuItem item) 
	{
        switch (item.getItemId()) 
        {
        //ɾ����ϵ��
        case DELETE_ID:
        	deleteContact();
            finish();
            break;
         //ɾ���մ����Ŀ���ϵ��
        case DISCARD_ID:
        	cancelContact();
        	finish();
            break;
        //ֱ�ӷ���
        case REVERT_ID:
        	finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	//ɾ����ϵ����Ϣ
	private void deleteContact() 
	{
		if (mCursor != null) 
		{
            mCursor.close();
            mCursor = null;
            getContentResolver().delete(mUri, null, null);
            nameText.setText("");
        }
	}
	//������Ϣ
	private void cancelContact() 
	{
		if (mCursor != null) 
		{
			deleteContact();
        }
        setResult(RESULT_CANCELED);
        finish();
	}
	//���� �������Ϣ
	private void updateContact() 
	{
		if (mCursor != null) 
		{
			mCursor.close();
            mCursor = null;
            ContentValues values = new ContentValues();
			values.put(ContactColumn.NAME, nameText.getText().toString());
			values.put(ContactColumn.MOBILENUM, mobileText.getText().toString());
			values.put(ContactColumn.HOMENUM, homeText.getText().toString());
			values.put(ContactColumn.ADDRESS, addressText.getText().toString());
			values.put(ContactColumn.EMAIL, emailText.getText().toString());
			//��������
            getContentResolver().update(mUri, values, null, null);
        }
        setResult(RESULT_CANCELED);
        finish();
	}
}