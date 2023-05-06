package com.compass.ux.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemGalleryBinding;
import com.compass.ux.databinding.ItemPaletteBinding;
import com.compass.ux.entity.MyGallyData;
import com.compass.ux.entity.PaletteSource;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;

import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.camera.Lens;


/**
 * 调色板
 */

public class PaletteAdapter extends BaseAdapter<String, ItemPaletteBinding> {

    List<PaletteSource> myGallyDataList;


    @Override
    protected void onBindingData(BaseHolder<ItemPaletteBinding> holder, String s, int position) {
        holder.getViewBinding().tvName.setText(myGallyDataList.get(position).getName());
        holder.getViewBinding().ivPalette.setBackgroundResource(myGallyDataList.get(position).getImgRes());
        holder.getViewBinding().cbPale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    myGallyDataList.get(position).setChecked(true);
                } else {
                    myGallyDataList.get(position).setChecked(false);
                }
            }
        });
        holder.getViewBinding().cbPale.setChecked(myGallyDataList.get(position).isChecked());

        holder.getViewBinding().ivPalette.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                for (int i = 0; i < myGallyDataList.size(); i++) {
                    myGallyDataList.get(i).setChecked(false);
                    setData(myGallyDataList);
                }
                if (holder.getViewBinding().cbPale.isChecked() == true) {
                    holder.getViewBinding().cbPale.setChecked(false);
                } else {
                    holder.getViewBinding().cbPale.setChecked(true);
                    if (Helper.isCameraModuleAvailable()) {
                        Camera camera = ApronApp.getCameraInstance();
                        if (camera.isMultiLensCameraSupported()&&Helper.isM300Product()) {
                            switch (position) {
                                case 0:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(0), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 1:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(1), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 2:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(2), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 3:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(3), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 4:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(4), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 5:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(5), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 6:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(10), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 7:
                                    camera.getLens(1).setThermalPalette(SettingsDefinitions.ThermalPalette.find(17), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;

                            }

                        } else {
                            switch (position) {
                                case 0:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(0), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 1:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(1), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 2:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(2), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 3:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(3), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 4:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(4), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 5:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(5), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 6:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(10), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;
                                case 7:
                                    camera.setThermalPalette(SettingsDefinitions.ThermalPalette.find(17), new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                    break;

                            }

                        }
                    } else {
                        ToastUtil.showToast("未检测到固件");
                    }

                }
            }
        });
    }

    @Override
    protected ItemPaletteBinding onBindingView(ViewGroup viewGroup) {
        ItemPaletteBinding itemGalleryBinding = ItemPaletteBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemGalleryBinding;
    }

    public void setData(List<PaletteSource> datas) {
        this.myGallyDataList = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (myGallyDataList != null) {
            return myGallyDataList.size();
        } else {
            return 0;
        }
    }
}