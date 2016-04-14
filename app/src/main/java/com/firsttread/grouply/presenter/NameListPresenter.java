package com.firsttread.grouply.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.firsttread.grouply.model.Group;
import com.firsttread.grouply.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class NameListPresenter {

    public ArrayList<CharSequence> sortFirstName(ArrayList<CharSequence> nameList){
        Collections.sort(nameList, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence cs1, CharSequence cs2) {
                return cs1.toString()
                        .toLowerCase()
                        .compareTo(cs2.toString().toLowerCase());
            }
        });
        return nameList;
    }

    public ArrayList<CharSequence> sortLastName(ArrayList<CharSequence> nameList){

        Collections.sort(nameList, new Comparator<CharSequence>() {
            @Override
            public int compare(CharSequence cs1, CharSequence cs2) {

                //to sort the last name: find substring starting at space+1 and sort there
                int spaceIndex_1 = cs1.toString().indexOf(" ") + 1;
                int spaceIndex_2 = cs2.toString().indexOf(" ") + 1;

                return cs1.toString()
                        .substring(spaceIndex_1)
                        .toLowerCase()
                        .compareTo(cs2.toString().substring(spaceIndex_2).toLowerCase());
            }
        });
        return nameList;
    }

    public ArrayList<CharSequence> sortShuffle(ArrayList<CharSequence> nameList){
        Collections.shuffle(nameList);
        return nameList;
    }

    public ArrayList<CharSequence> sortFlip(ArrayList<CharSequence> nameList){
        Collections.reverse(nameList);
        return nameList;
    }

    public void saveGroup(final ArrayList<CharSequence> nameList,final String groupName,Context context){

        //ToDo: erase context

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        Group groupObject = realm.createObject(Group.class);
        groupObject.setName(groupName);
        realm.commitTransaction();

        realm.beginTransaction();
        for(CharSequence name:nameList){
            Person personObject = realm.createObject(Person.class);
            personObject.setName(name.toString());
            personObject.setGroup(groupName);
        }
        realm.commitTransaction();

        RealmResults<Group> groups = realm.allObjects(Group.class);
        for(Group group:groups){
            Log.d("Result Dump: ",group.getName());
        }

        realm.close();

    }


    public CharSequence[] getGroupList(){

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Group> query = realm.where(Group.class);

        RealmResults<Group> result = query.findAll();

        CharSequence[] groupList = new CharSequence[result.size()];

        for(int i=0;i<result.size();i++){
            groupList[i] = result.get(i).getName();
        }

        return groupList;
    }

    //ToDo: remove this
    public void deleteRealm(){
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        realm.clear(Group.class);
        realm.clear(Person.class);
        realm.commitTransaction();

        realm.close();
    }

}
