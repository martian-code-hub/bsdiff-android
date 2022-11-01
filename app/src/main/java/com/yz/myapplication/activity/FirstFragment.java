package com.yz.myapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.martian.bsdiff.BsdiffUtils;
import com.yz.myapplication.R;
import com.yz.myapplication.databinding.FragmentFirstBinding;
import com.yz.myapplication.util.MD5Util;

import java.io.File;



public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private String path_1 = "";
    private String path_2 = "";
    private String path_3 = "";
    private String path_4 = "";

    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initView();
        initListener();

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    private void initView() {
        String root = this.getActivity().getApplication().getFilesDir().getAbsolutePath();
        File rootRoot = new File(root);
        if(!rootRoot.exists()){
            rootRoot.mkdirs();
        }
        path_1 = root+"/1.zip";
        path_2 = root+"/2.zip";
        path_3 = root+"/3.patch";
        path_4 = root+"/4.zip";
        binding.textview1.setText("地址："+root);
        binding.textview1.setText("1包md5："+ MD5Util.getFileMD5(new File(path_1)));
        binding.textview2.setText("2包md5："+ MD5Util.getFileMD5(new File(path_2)));

        binding.textview3.setText("patch包md5："+ MD5Util.getFileMD5(new File(path_3)));
        binding.textview4.setText("合成包md5："+ MD5Util.getFileMD5(new File(path_4)));
    }

    private void initListener() {
        binding.button1.setOnClickListener(v -> {

            new Thread(() -> {
                BsdiffUtils.diff(path_1,path_2,path_3);
                handler.post(() -> {
                    binding.textview3.setText("patch包md5："+ MD5Util.getFileMD5(new File(path_3)));
                });
            }).start();
        });

        binding.button2.setOnClickListener(v -> {
            new Thread(() -> {
                BsdiffUtils.patch(path_1,path_3,path_4);
                handler.post(() -> {
                    binding.textview4.setText("合成包md5："+ MD5Util.getFileMD5(new File(path_4)));
                });
            }).start();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}