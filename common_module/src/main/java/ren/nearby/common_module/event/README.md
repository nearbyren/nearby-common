1.发送
        RxBus.getDefault().post(new CheckEvent(1, "3"));
2.接收
        rxSubscription = RxBus.getDefault().toObservable(CheckEvent.class)
                .subscribe(new Action1<CheckEvent>() {
                    @Override
                    public void call(CheckEvent checkEvent) {
                        viewPager.setCurrentItem(2);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.show(MainActivity.this, "出现问题");
                    }
                });


    }
3.注销
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }

    }