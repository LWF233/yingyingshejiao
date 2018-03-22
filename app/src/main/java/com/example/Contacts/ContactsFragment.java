package com.example.Contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.R;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMContactManager;
import com.hyphenate.chat.adapter.EMAChatManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsFragment extends Fragment {
    private Button contactButton;
    private Button groupButton;
    private Button addButton;
    private Fragment fragment;
    private Fragment currentFragment;
    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_contact, container, false);
        FragmentManager manager1 = getActivity().getSupportFragmentManager();
        addFragment("Contact");
        //点击对应按钮更换对应fragment

        contactButton=view.findViewById(R.id.Contact);
        groupButton=view.findViewById(R.id.Group);
        addButton=view.findViewById(R.id.AddButton);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment("Contact");
            }
        });


        groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment("Group");
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment("Search");

            }
        });
        return view;
    }


    private void addFragment(String fTag) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //判断这个标签是否存在Fragment对象,如果存在则返回，不存在返回null
        fragment =  manager.findFragmentByTag(fTag);
        // 如果这个fragment不存于栈中
        if (fragment == null) {
            //初始化Fragment事物

            //根据RaioButton点击的Button传入的tag，实例化，添加显示不同的Fragment
            if (fTag.equals("Contact")) {
                final EaseContactListFragment fragment1 = new EaseContactListFragment();
                new Thread() {//需要在子线程中调用
                    @Override
                    public void run() {
                        //需要设置联系人列表才能启动fragment
                        fragment1.setContactsMap(getContact());
                    }
                }.start();
                fragment=fragment1;
            } else if (fTag.equals("Group")) {
                fragment = new GroupFragment();
            } else if (fTag.equals("Search")) {
                fragment = new SearchFragment();
            }
            //在添加之前先将上一个Fragment隐藏掉
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.MyFragment, fragment, fTag);
            transaction.commit();
            //更新可见
            currentFragment = fragment;
        } else {
            //如果添加的Fragment已经存在，则将隐藏掉的Fragment再次显示,其余当前
            transaction = manager.beginTransaction();
            transaction.hide(currentFragment);
            transaction.show(fragment);
            //更新可见
            currentFragment = fragment;
            transaction.commit();


        }


    }

    private Map<String, EaseUser> getContact() {
        Map<String, EaseUser> map = new HashMap<>();
        try {
            List<String> userNames =EMClient.getInstance().contactManager().getAllContactsFromServer();
//            KLog.e("......有几个好友:" + userNames.size());
            for (String userId : userNames) {
//                KLog.e("好友列表中有 : " + userId);
                map.put(userId, new EaseUser(userId));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return map;
    }

}
