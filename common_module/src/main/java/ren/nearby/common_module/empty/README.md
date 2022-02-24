    https://github.com/Syehunter/MaterialPageStateLayout
    //父布局
     View view = inflater.inflate(R.layout.home_one, container, false); 
     //父布局父节点
     LinearLayout parent = (LinearLayout) view.findViewById(R.id.parent);
     //需要添加的内容
     View content = inflater.inflate(R.layout.home_one_content, container, false);
     //初始化容器
     mLayout = new PageStateLayout(getActivity().getApplicationContext());
     mLayout
        .setOnEmptyListener(mOnEmptyListener)//空数据监听点击可刷新
        .setOnErrorListener(mOnErrorListener)//异常监听点击可刷新
        .load(parent, content);//设置内容
