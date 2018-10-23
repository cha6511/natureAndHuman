package human.nature.customerorderapp.EventBus;


public class Events {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";
    public static final String DIALOG_CLOSE = "DIALOG_CLOSE";

    public static final String REFRESH_ORDER_LIST = "REFRESH_ORDER_LIST";
    public static final String REFRESH_NOTI_LIST = "REFRESH_NOTI_LIST";


    public static class Msg{
        private String msg;
        public Msg(String msg){
            this.msg = msg;
        }
        public String getMsg(){
            return msg;
        }
    }

//    public static class SendMonthlyFestivalData{
//        private MonthlyFestivalListData data;
//        public SendMonthlyFestivalData(MonthlyFestivalListData data){
//            this.data = data;
//        }
//        public MonthlyFestivalListData getData() {
//            return data;
//        }
//    }

}
