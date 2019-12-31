package com.example.doodling.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doodling.DataBase;
import com.example.doodling.R;
import com.example.doodling.View.DrawingGroupAdapter;
import com.example.doodling.View.GroupDrawing;
import com.example.doodling.View.MyAdapter;
import com.example.doodling.androidutils.AppUtils;
import com.example.doodling.androidutils.ImageUtils;
import com.example.doodling.androidutils.ToastUtils;
import com.example.doodling.paintType.ActionType;
import com.example.doodling.View.Drawing;
import com.example.doodling.View.DrawingView;
import com.example.doodling.utils.FileUtils;
import com.example.doodling.utils.ShareUtils;
import com.example.doodling.widget.shape.DrawShape;
import com.example.doodling.widget.shape.Type;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WhiteBoardActivity extends AppCompatActivity {
    public static final String PACKAGE_BAIDU_DISK = "com.baidu.netdisk";
    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.tv_history)
    ImageButton history;
    @BindView(R.id.type)
    ImageButton type;
    @BindView(R.id.draw_btn)
    ImageButton pen;
    @BindView(R.id.erase_btn)
    ImageButton erase;
    @BindView(R.id.black)
    ImageButton black;
    @BindView(R.id.red)
    ImageButton red;
    @BindView(R.id.blue)
    ImageButton blue;
    @BindView(R.id.small)
    ImageButton small;
    @BindView(R.id.large)
    ImageButton large;
    @BindView(R.id.reset)
    ImageButton clear;
    @BindView(R.id.save)
    ImageButton save;
    @BindView(R.id.new_file)
    ImageButton newFile;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.left_view)
    RelativeLayout left_view;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.board_view)
    BoardView mBoardView;
    @BindView(R.id.top_set)
    LinearLayout TopSetting;
    @BindView(R.id.picName)
    TextView picName;
    @BindView(R.id.listView_items)
    ListView mItemListview;
    private static final String TAG = "MainActivity";
    private DataBase dataBase;
    private Drawing drawing;
    private String name;
    private List<Drawing> drawings = new ArrayList<>();
    private AlertDialog ColorDialog;
    private AlertDialog PaintDialog;
    private AlertDialog ShapeDialog;
    private AlertDialog saveImageDialog;
    private AlertDialog newFileDialog;
    private AlertDialog clearDialog;
    private AlertDialog insNetDiskDialog;
    private AlertDialog insWchatDialog;
    private MyAdapter adapter;
    Cursor cursor;
    private Bitmap mBitmap;
    String oldName = "";
    //2019-10-29 w
    // 触摸屏幕超过一定时间才判断为需要隐藏设置面板
    private Runnable mHideDelayRunnable;
    // 触摸屏幕超过一定时间才判断为需要显示设置面板
    private Runnable mShowDelayRunnable;
    public long mChangePanelVisibilityDelay = 200;//触发时间
    public long mChangePanelGoneDelay = 800;
    private AlphaAnimation mViewShowAnimation, mViewHideAnimation; // view隐藏和显示时用到的渐变动画
    EditText editText;
    EditText newEditText;
    //2019-10-29
    private DrawingGroupAdapter groupAdapter;
    HashMap<Long, List<Drawing>> groupMap = new HashMap<>();
    List<GroupDrawing> groupList = new ArrayList<>();
    long currentGroupId = 0;
    private List<Drawing> singelGroupDrawins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        drawing = new Drawing();
        dataBase = new DataBase(this);
        pen.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
        black.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
        small.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
        initView();
        update();

    }

    private void update() {
        ArrayList<Drawing> dbDrawings = dataBase.getDrawing();
        drawings.clear();
        drawings.addAll(dbDrawings);
        Collections.reverse(drawings);
        divideGroup(drawings);
        groupAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    //add 2019 1030 yhm start
    public void divideGroup(List<Drawing> drawings) {
        groupMap.clear();
        groupList.clear();
        for (Drawing drawingItem : drawings) {
            if (groupMap.containsKey(drawingItem.getGroupId())) {
                groupMap.get(drawingItem.getGroupId()).add(drawingItem);
            } else {
                ArrayList<Drawing> itemList = new ArrayList<>();
                itemList.add(drawingItem);
                groupMap.put(drawingItem.getGroupId(), itemList);
                groupList.add(new GroupDrawing(drawingItem.getGroupId(), groupMap.get(drawingItem.getGroupId())));
            }
        }
    }
    //add 2019 1030 yhm end

    private void initView() {
        //隐藏设置面板
        mBoardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mChangePanelVisibilityDelay > 0) {
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            TopSetting.removeCallbacks(mHideDelayRunnable);
                            TopSetting.removeCallbacks(mShowDelayRunnable);
                            //触摸屏幕超过一定时间才判断为需要隐藏设置面板
                            TopSetting.postDelayed(mHideDelayRunnable, mChangePanelVisibilityDelay);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            TopSetting.removeCallbacks(mHideDelayRunnable);
                            TopSetting.removeCallbacks(mShowDelayRunnable);
                            //离开屏幕超过一定时间才判断为需要显示设置面板
                            TopSetting.postDelayed(mShowDelayRunnable, mChangePanelGoneDelay);
                            break;
                    }
                }
                return false;
            }
        });

        mViewShowAnimation = new AlphaAnimation(0, 1);
        mViewShowAnimation.setDuration(150);
        mViewHideAnimation = new AlphaAnimation(1, 0);
        mViewHideAnimation.setDuration(150);
        mHideDelayRunnable = new Runnable() {
            public void run() {
                hideView(TopSetting);
            }

        };
        mShowDelayRunnable = new Runnable() {
            public void run() {
                showView(TopSetting);
            }
        };
        editText = new EditText(this);
        newEditText = new EditText(this);

        //yhm start
        //一级菜单
        groupAdapter = new DrawingGroupAdapter(this, groupList);
        listView.setAdapter(groupAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                groupAdapter.setDefSelect(i);
                GroupDrawing groupDrawing = groupList.get(i);
                drawing = groupDrawing.getDrawingItems().get(0);
                currentGroupId = groupDrawing.getGroupId();
                oldName = drawing.getName();
                picName.setText(oldName + "-" + drawing.getDate());
                dataBase.getMap(drawing);
                mBitmap = drawing.getBitmap();
                mBoardView.updateDrawFromBitmap(mBitmap);

                singelGroupDrawins.clear();
                singelGroupDrawins.addAll(groupDrawing.getDrawingItems());
                adapter.setDefSelect(0);
                adapter.notifyDataSetChanged();
                mItemListview.setVisibility(View.VISIBLE);
            }
        });

        adapter = new MyAdapter(this, singelGroupDrawins);
        mItemListview.setAdapter(adapter);
        mItemListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setDefSelect(position);
                drawing = singelGroupDrawins.get(position);
                currentGroupId = drawing.getGroupId();
                oldName = drawing.getName();
                picName.setText(oldName + "-" + drawing.getDate());
                dataBase.getMap(drawing);
                mBitmap = drawing.getBitmap();
                mBoardView.updateDrawFromBitmap(mBitmap);
            }
        });

        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                view = getWindow().peekDecorView();
                if (null != view) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });
        //yhm end
    }

    private void showView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }

        view.clearAnimation();
        view.startAnimation(mViewShowAnimation);
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }
        view.clearAnimation();
        view.startAnimation(mViewHideAnimation);
        view.setVisibility(View.GONE);
    }

    //2019-10-31-modify
    @OnClick({R.id.draw_btn, R.id.erase_btn, R.id.black, R.id.red, R.id.blue, R.id.small, R.id.large, R.id.reset, R.id.save, R.id.search, R.id.left_view, R.id.tv_history, R.id.new_file,R.id.sharenetdisk,R.id.sharewchat})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draw_btn:
                mBoardView.setDrawType(Type.CURVE);
                pen.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                erase.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.black:
                DrawShape.mPaintColor = new Integer(Color.BLACK);
                black.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                red.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                blue.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.red:
                DrawShape.mPaintColor = new Integer(Color.RED);
                red.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                black.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                blue.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.blue:
                DrawShape.mPaintColor = new Integer(Color.BLUE);
                blue.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                black.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                red.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;

            case R.id.small:
                DrawShape.mPaintWidth = 4;
                small.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                large.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.large:
                DrawShape.mPaintWidth = 8;
                large.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                small.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.reset:
                clearImage();
                break;
            case R.id.erase_btn:
                mBoardView.setDrawType(Type.WIPE);
                erase.setBackgroundResource(R.drawable.doodle_shape_circle_pressed);
                pen.setBackgroundResource(R.drawable.doodle_shape_circle_normal);
                break;
            case R.id.save:
                saveImage();
                break;
            case R.id.sharenetdisk:
                saveAndShareToBaiduNetdisk();
                break;
            case R.id.sharewchat:
                saveAndShareToWchat();
                break;
            case R.id.new_file:
                createNewFile();
                break;
            case R.id.search:
                Search();
                break;
            case R.id.tv_history:
                if (drawer.isDrawerOpen(left_view)) {
                    drawer.closeDrawer(left_view);
                } else {
                    drawer.openDrawer(left_view);
                }
                break;
        }
    }

    private void createNewFile() {
        newEditText.setText(oldName);
        if (!"".equals(oldName)) {
            newEditText.setSelection(oldName.length());
        }
        if (newFileDialog == null) {
            newFileDialog = new AlertDialog.Builder(this)
                    .setTitle("是否保存当前记录？")
                    .setView(newEditText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name = newEditText.getText().toString();
                            if (name.isEmpty()) {
                                showMessage("标题不能为空！");
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                Long saveTime = System.currentTimeMillis();
                                Date date = new Date(saveTime);
                                Bitmap bitmap = mBoardView.getDoodleBitmap();
                                drawing.setName(name);
                                drawing.setDate(simpleDateFormat.format(date));
                                drawing.setDateTiem(saveTime);
                                if (currentGroupId != 0 && !"".equals(oldName)) {
                                    drawing.setGroupId(currentGroupId);
                                    currentGroupId = 0;
                                } else {
                                    drawing.setGroupId(saveTime);
                                    currentGroupId = saveTime;
                                }
                                //保存内容到数据库
                                dataBase.addDrawing(drawing, BitmapToBytes(bitmap));
                                groupAdapter.setDefSelect(-1);
                                adapter.setDefSelect(-1);
                                mItemListview.setVisibility(View.GONE);
                                //标题清空
                                oldName = "";
                                picName.setText("");
                                showMessage("保存成功！");
                                //清除屏幕内容
                                mBoardView.clearScreen();
                                update();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentGroupId = 0;
                            groupAdapter.setDefSelect(-1);
                            adapter.setDefSelect(-1);
                            mItemListview.setVisibility(View.GONE);
                            oldName = "";
                            picName.setText("");
                            mBoardView.clearScreen();
                            update();
                        }
                    })
                    .create();

        }
        newFileDialog.show();

    }

    public void Search() {
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    View view = getWindow().peekDecorView();
                    if (null != view) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String editText = search.getText().toString().trim();
                    //通过关键字搜索出对应的组
                    ArrayList<Drawing> searchDrawings = dataBase.getSearchDrawing(editText);
                    Collections.reverse(searchDrawings);
                    groupList.clear();
                    for (Drawing searchDrawing : searchDrawings) {
                        GroupDrawing searchGroup = new GroupDrawing(searchDrawing.getGroupId(), groupMap.get(searchDrawing.getGroupId()));
                        if (!groupList.contains(searchGroup)) {
                            groupList.add(searchGroup);
                        }
                    }
                    groupAdapter.setDefSelect(-1);
                    mItemListview.setVisibility(View.GONE);
                }

                return false;
            }
        });
    }

    //设置画笔形状（暂未用到）
    private void showShapeDialog() {
        if (ShapeDialog == null) {
            ShapeDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择形状")
                    .setSingleChoiceItems(new String[]{"路径", "直线", "矩形", "圆形"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            mBoardView.setDrawType(Type.CURVE);
                                            break;
                                        case 1:
                                            mBoardView.setDrawType(Type.LINE);
                                            break;
                                        case 2:
                                            mBoardView.setDrawType(Type.RECTANGLE);
                                            break;
                                        case 3:
                                            mBoardView.setDrawType(Type.OVAL);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        ShapeDialog.show();
    }

    private void showColorDialog() {

        if (ColorDialog == null) {
            ColorDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择颜色")
                    .setSingleChoiceItems(new String[]{"黑色", "红色", "蓝色", "黄色"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            DrawShape.mPaintColor = new Integer(Color.BLACK);
                                            break;
                                        case 1:
                                            DrawShape.mPaintColor = new Integer(Color.RED);
                                            break;
                                        case 2:
                                            DrawShape.mPaintColor = new Integer(Color.BLUE);
                                            break;
                                        case 3:
                                            DrawShape.mPaintColor = new Integer(Color.YELLOW);
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();
        }
        ColorDialog.show();
    }

    private void showSizeDialog() {
        if (PaintDialog == null) {
            PaintDialog = new AlertDialog.Builder(this)
                    .setTitle("请选择画笔大小")
                    .setSingleChoiceItems(new String[]{"小", "中", "大", "超大"}, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            DrawShape.mPaintWidth = 10;
                                            break;
                                        case 1:
                                            DrawShape.mPaintWidth = 15;
                                            break;
                                        case 2:
                                            DrawShape.mPaintWidth = 20;
                                            break;
                                        case 3:
                                            DrawShape.mPaintWidth = 25;
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create();

        }
        PaintDialog.show();
    }

    //2019-10-31-modify
    //清除画板
    private void clearImage() {
        if (clearDialog == null) {
            clearDialog = new AlertDialog.Builder(this)
                    .setTitle("请先保存当前内容，清除后无法恢复，您确定清除吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            groupAdapter.setDefSelect(-1);
                            adapter.setDefSelect(-1);
                            mItemListview.setVisibility(View.GONE);
                            mBoardView.clearScreen();
                        }
                    }).setNegativeButton("取消", null)
                    .create();
        }
        clearDialog.show();
    }

    //2019-10-31-modify
    //保存到数据库
    private void saveImage() {
        editText.setText(oldName);
        if (!"".equals(oldName)) {
            editText.setSelection(oldName.length());
        }
        if (saveImageDialog == null) {
            saveImageDialog = new AlertDialog.Builder(this)
                    .setTitle("请输入标题！")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name = editText.getText().toString();
                            if (name.isEmpty()) {
                                showMessage("标题不能为空！");
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                Long saveTime = System.currentTimeMillis();
                                Date date = new Date(saveTime);
                                Bitmap bitmap = mBoardView.getDoodleBitmap();
                                drawing.setName(name);
                                drawing.setDate(simpleDateFormat.format(date));
                                drawing.setDateTiem(saveTime);
                                if (currentGroupId != 0) {
                                    drawing.setGroupId(currentGroupId);
                                } else {
                                    drawing.setGroupId(saveTime);
                                    currentGroupId = saveTime;
                                }
                                //内容保存到数据库
                                dataBase.addDrawing(drawing, BitmapToBytes(bitmap));
                                showMessage("保存成功！");
                                //列表选中状态清除
                                groupAdapter.setDefSelect(-1);
                                adapter.setDefSelect(-1);
                                mItemListview.setVisibility(View.GONE);
                                //更新标题
                                oldName = name;
                                picName.setText(name + "-" + simpleDateFormat.format(date));
                                //更新数据库中新添加的内容
                                update();
                            }
                        }
                    }).setNegativeButton("取消", null)
                    .create();

        }
        saveImageDialog.show();
    }
    /**
     * 保存到百度云
     *
     */
    public void saveAndShareToBaiduNetdisk() {
        if (!AppUtils.isInstallApp(this, PACKAGE_BAIDU_DISK)) {
            if (insNetDiskDialog == null) {
                insNetDiskDialog = new AlertDialog.Builder(this)
                        .setMessage("未能检测到百度网盘，请先安装百度网盘")
                        .setPositiveButton("确定", null).create();
            }
            insNetDiskDialog.show();
            return;
        }
        editText.setText(oldName);
        if (!"".equals(oldName)) {
            editText.setSelection(oldName.length());
        }
        if (saveImageDialog == null) {
            saveImageDialog = new AlertDialog.Builder(this)
                    .setTitle("请输入标题！")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name = editText.getText().toString();
                            if (name.isEmpty()) {
                                showMessage("标题不能为空！");
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                Long saveTime = System.currentTimeMillis();
                                Date date = new Date(saveTime);
                                Bitmap bitmap = mBoardView.getDoodleBitmap();
                                drawing.setName(name);
                                drawing.setDate(simpleDateFormat.format(date));
                                drawing.setDateTiem(saveTime);
                                if (currentGroupId != 0) {
                                    drawing.setGroupId(currentGroupId);
                                } else {
                                    drawing.setGroupId(saveTime);
                                    currentGroupId = saveTime;
                                }
                                //内容保存到数据库
                                dataBase.addDrawing(drawing, BitmapToBytes(bitmap));
                                //列表选中状态清除
                                groupAdapter.setDefSelect(-1);
                                adapter.setDefSelect(-1);
                                mItemListview.setVisibility(View.GONE);
                                //更新标题
                                oldName = name;
                                picName.setText(name + "-" + simpleDateFormat.format(date));
                                //更新数据库中新添加的内容
                                update();
                                shareToBaiduNetdisk(name);
                            }
                        }
                    }).setNegativeButton("取消", null)
                    .create();

        }
        saveImageDialog.show();
    }
    /**
     * 保存到百度云
     *
     */
    public void shareToBaiduNetdisk(String name) {
        Bitmap bitmap = mBoardView.getDoodleBitmap();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/doodleview/" + name+ ".png";
        if (!new File(filePath).exists()) {
            new File(filePath).getParentFile().mkdir();
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
            bitmap= ImageUtils.changeColor(bitmap);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShareUtils.sharePictureToBaiduDisk(this,new File(filePath));
    }
    /**
     * 保存到百度云
     *
     */
    public void shareToWchat(String name) {
        Bitmap bitmap = mBoardView.getDoodleBitmap();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/doodleview/" + name+ ".png";
        if (!new File(filePath).exists()) {
            new File(filePath).getParentFile().mkdir();
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
            bitmap= ImageUtils.changeColor(bitmap);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShareUtils.sharePictureToWechatFriend(this,new File(filePath));
    }
    /**
     * 保存到百度云
     *
     */
    public void saveAndShareToWchat() {
        if (!AppUtils.isInstallApp(this, PACKAGE_WECHAT)) {
            if (insWchatDialog == null) {
                insWchatDialog = new AlertDialog.Builder(this)
                        .setMessage("未能检测到微信，请先安装微信")
                        .setPositiveButton("确定", null).create();
            }
            insWchatDialog.show();
            return;
        }
        editText.setText(oldName);
        if (!"".equals(oldName)) {
            editText.setSelection(oldName.length());
        }
        if (saveImageDialog == null) {
            saveImageDialog = new AlertDialog.Builder(this)
                    .setTitle("请输入标题！")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            name = editText.getText().toString();
                            if (name.isEmpty()) {
                                showMessage("标题不能为空！");
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                Long saveTime = System.currentTimeMillis();
                                Date date = new Date(saveTime);
                                Bitmap bitmap = mBoardView.getDoodleBitmap();
                                drawing.setName(name);
                                drawing.setDate(simpleDateFormat.format(date));
                                drawing.setDateTiem(saveTime);
                                if (currentGroupId != 0) {
                                    drawing.setGroupId(currentGroupId);
                                } else {
                                    drawing.setGroupId(saveTime);
                                    currentGroupId = saveTime;
                                }
                                //内容保存到数据库
                                dataBase.addDrawing(drawing, BitmapToBytes(bitmap));
                                //列表选中状态清除
                                groupAdapter.setDefSelect(-1);
                                adapter.setDefSelect(-1);
                                mItemListview.setVisibility(View.GONE);
                                //更新标题
                                oldName = name;
                                picName.setText(name + "-" + simpleDateFormat.format(date));
                                //更新数据库中新添加的内容
                                update();
                                shareToBaiduNetdisk(name);
                            }
                        }
                    }).setNegativeButton("取消", null)
                    .create();

        }
        saveImageDialog.show();
    }
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //保存图片到文件夹
    public static boolean saveImageByPNG(Bitmap bitmap) {
        String Path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/drawingView/" + System.currentTimeMillis() + ".png";
        if (!new File(Path).exists()) {
            new File(Path).getParentFile().mkdir();
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(Path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public byte[] BitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
