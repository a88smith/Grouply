/*
    Copyright (c) 2016 Anthony Smith

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.firsttread.grouply;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.firsttread.grouply.presenter.IntSinglePresenter;
import com.firsttread.grouply.presenter.SingleGroupPresenter;
import com.firsttread.grouply.view.IntSingleView;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SingleGroup extends AppCompatActivity implements IntSingleView {

    private IntSinglePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_group);

        presenter = new SingleGroupPresenter(this);

        //sets up a realm and sets it as default
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);

        //setting up file picker
        DialogProperties properties = new DialogProperties();
        setupFilePickerDialog(properties);

        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String actTitle = sharedPref.getString(getString(R.string.act_title),"Grouply");
        setTitle(actTitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*to maintain list of names on back press*/

        NameListFragment frag =
                (NameListFragment) getSupportFragmentManager().findFragmentById(R.id.list);

        if(frag.saveOnPressBackButton() > 0){
            SharedPreferences sharedPref =
                    getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.act_title),getTitle().toString());
            editor.apply();
        }
    }


    @Override
    public void onShowDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if(prev != null){
            ft.remove(prev);
        }

        ft.addToBackStack(null);
        DialogFragment newFragment = AddNameDialog.newInstance();
        newFragment.show(ft,"dialog");
    }

    @Override
    public void onClearRealm(){

        new AlertDialog.Builder(this)
                .setTitle("Delete All Saved Groups?")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.clearRealm();
                        Toast.makeText(SingleGroup.this,"All Groups Deleted",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void onDeleteGroup(){

        CharSequence[] groupList = presenter.getGroupList();

        new AlertDialog.Builder(this)
                .setTitle("Choose a Group to Delete")
                .setItems(groupList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView lv = ((AlertDialog)dialog).getListView();
                        String groupName = lv.getItemAtPosition(which).toString();

                        presenter.deleteGroup(groupName);

                        Toast.makeText(SingleGroup.this,"Group Deleted",Toast.LENGTH_LONG).show();
                    }
                }).show();




    }

    @Override
    public void onChangeTitle(String newTitle) {
        setTitle(newTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.add_item:
                presenter.showDialog();
                return true;
            case R.id.clear_groups:
                onClearRealm();
                return true;
            case R.id.delete_group:
                onDeleteGroup();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setupFilePickerDialog(DialogProperties properties) {

        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        FilePickerDialog dialog = new FilePickerDialog(this,properties);
        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files are in an array of the paths of files selected by the Application User.

            }
        });

    }

}






















