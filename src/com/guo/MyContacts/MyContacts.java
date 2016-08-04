package com.guo.MyContacts;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MyContacts extends ListActivity
{
	private static final int AddContact_ID = Menu.FIRST;
	private static final int DELEContact_ID = Menu.FIRST+2;
	private static final int EXITContact_ID = Menu.FIRST+3;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		//Ϊintent������
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContactsProvider.CONTENT_URI);
        }
        //���ò˵����������
        getListView().setOnCreateContextMenuListener(this);
        //���ñ���ͼƬ
        getListView().setBackgroundResource(R.drawable.bg);
        //��ѯ�����������ϵ�˵�����
        Cursor cursor = managedQuery(getIntent().getData(), ContactColumn.PROJECTION, null, null,null);

        //ע��ÿ���б��ʾ��ʽ ������ + �ƶ��绰
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
			android.R.layout.simple_list_item_2,
			cursor,
			new String[] {ContactColumn.NAME, ContactColumn.MOBILENUM },
			new int[] { android.R.id.text1, android.R.id.text2 });

        setListAdapter(adapter);
	}
	//��Ӳ˵�ѡ��
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        super.onCreateOptionsMenu(menu);
        //�����ϵ��
        menu.add(0, AddContact_ID, 0, R.string.add_user)
        	.setShortcut('3', 'a')
        	.setIcon(R.drawable.add);

        //�˳�����
        menu.add(0, EXITContact_ID, 0, R.string.exit)
    		.setShortcut('4', 'd')
    		.setIcon(R.drawable.exit);
        return true;
        
    }
    
    //����˵�����
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
        case AddContact_ID:
            //�����ϵ��
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        case EXITContact_ID:
        	//�˳�����
        	this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //��̬�˵�����
    //�����Ĭ�ϲ���Ҳ���������ﴦ��
    protected void onListItemClick(ListView l, View v, int position, long id)   
    {   
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);       
    	//�鿴��ϵ��
    	startActivity(new Intent(Intent.ACTION_VIEW, uri));       
    }   
    
    //���������Ĳ˵�
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
	{
		AdapterView.AdapterContextMenuInfo info;
		try
		{
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		}
		catch (ClassCastException e)
		{
			return;
		}
		//�õ�������������
		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null)
		{
			return;
		}

		menu.setHeaderTitle(cursor.getString(1));
		//���ɾ���˵�
		menu.add(0, DELEContact_ID, 0, R.string.delete_user);
	}
    //�����б����ĺ���
    @Override
    public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info;
		try
		{
			//���ѡ�е������Ϣ
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		}
		catch (ClassCastException e)
		{
			return false;
		}

		switch (item.getItemId())
		{
			//ɾ������
			case DELEContact_ID:
			{
				//ɾ��һ����¼
				Uri noteUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
				getContentResolver().delete(noteUri, null, null);
				return true;
			}
		}
		return false;
	}
}