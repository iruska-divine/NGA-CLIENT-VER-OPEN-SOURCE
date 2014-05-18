package gov.anzong.androidnga.activity;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import gov.anzong.androidnga.R;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ProfileData;
import sp.phone.bean.ReputationData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.bean.adminForumsData;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.interfaces.OnProfileLoadFinishedListener;
import sp.phone.task.AvatarLoadTask;
import sp.phone.task.JsonProfileLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class FlexibleProfileActivity extends SwipeBackAppCompatActivity
implements OnProfileLoadFinishedListener,AvatarLoadCompleteCallBack,
PerferenceConstant{
	private static final String TAG= "FlexibleProfileActivity";
	private String mode,params;
	private View view;
	private final Object lock = new Object();
	private final HashSet<String> urlSet = new HashSet<String>();
	private Object mActionModeCallback = null;
	private TextView basedata_title,user_id,user_name,user_email_title,user_email,user_tel_title,user_tel,user_group,user_posttotal;
	private TextView user_money_gold,user_money_silver,user_money_copper,user_title,user_state,user_registertime,user_lastlogintime;
	private ImageView avatargold,avatarsilver,avatarcopper,avatarImage;
	private TextView avatar_title,sign_title,admin_title,fame_title,search_title,admin2_title,fame2_title,user_shutup_title,user_shutup;
	private WebView signwebview,adminwebview,famewebview;
	private Button topic_button,reply_button;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		activeActionMode();
		Intent intent = this.getIntent();
		mode=intent.getStringExtra("mode");
		if(!StringUtil.isEmpty(mode)){
		if(mode.equals("uid")){
			params="uid="+intent.getStringExtra("uid");
		}else{
			params="username="+StringUtil.encodeUrl(intent.getStringExtra("username"),"gbk");
		}}else{
			params="uid=0";
		}
		this.setContentView(R.layout.profile);
		this.setTitle("�û���Ϣ");
		view = findViewById(R.id.scroll_profile);
		view.setVisibility(View.GONE);
		basedata_title = (TextView) view.findViewById(R.id.basedata_title);
		user_id = (TextView) view.findViewById(R.id.user_id);
		user_name = (TextView) view.findViewById(R.id.user_name);
		user_email_title = (TextView) view.findViewById(R.id.user_email_title);
		user_email = (TextView) view.findViewById(R.id.user_email);
		user_tel_title = (TextView) view.findViewById(R.id.user_tel_title);
		user_tel = (TextView) view.findViewById(R.id.user_tel);
		user_group = (TextView) view.findViewById(R.id.user_group);
		user_posttotal = (TextView) view.findViewById(R.id.user_posttotal);
		user_money_gold = (TextView) view.findViewById(R.id.user_money_gold);
		user_money_silver = (TextView) view.findViewById(R.id.user_money_silver);
		user_money_copper = (TextView) view.findViewById(R.id.user_money_copper);
		user_title = (TextView) view.findViewById(R.id.user_title);
		user_state = (TextView) view.findViewById(R.id.user_state);
		user_registertime = (TextView) view.findViewById(R.id.user_registertime);
		user_lastlogintime = (TextView) view.findViewById(R.id.user_lastlogintime);
		avatar_title = (TextView) view.findViewById(R.id.avatar_title);
		sign_title = (TextView) view.findViewById(R.id.sign_title);
		admin_title = (TextView) view.findViewById(R.id.admin_title);
		admin2_title = (TextView) view.findViewById(R.id.admin2_title);
		fame_title = (TextView) view.findViewById(R.id.fame_title);
		fame2_title = (TextView) view.findViewById(R.id.fame2_title);
		search_title = (TextView) view.findViewById(R.id.search_title);
		avatargold = (ImageView) view.findViewById(R.id.avatargold);
		avatarsilver = (ImageView) view.findViewById(R.id.avatarsilver);
		avatarcopper = (ImageView) view.findViewById(R.id.avatarcopper);
		avatarImage = (ImageView) view.findViewById(R.id.avatarImage);
		signwebview = (WebView) view.findViewById(R.id.signwebview);
		adminwebview = (WebView) view.findViewById(R.id.adminwebview);
		famewebview = (WebView) view.findViewById(R.id.famewebview);
		topic_button = (Button) view.findViewById(R.id.topic_button);
		reply_button = (Button) view.findViewById(R.id.reply_button);
		user_shutup_title = (TextView) view.findViewById(R.id.user_shutup_title);
		user_shutup = (TextView) view.findViewById(R.id.user_shutup);
		refresh();
	}

	void refresh() {
		JsonProfileLoadTask task = new JsonProfileLoadTask(this,this);
		ActivityUtil.getInstance().noticeSaying(this);
		task.execute(params);
	}// ��ȡJSON��

	
	void writetopage(final ProfileData ret){
		String username=ret.get_username();
		setTitle(username+"���û���Ϣ");
		basedata_title.setText(":: "+username+" �Ļ�����Ϣ ::");
		avatar_title.setText(":: "+username+" ��ͷ�� ::");
		sign_title.setText(":: "+username+" ��ǩ�� ::");
		admin_title.setText(":: "+username+" �Ĺ���Ȩ�� ::");
		admin2_title.setText(username+" ӵ�й���ԱȨ�� ����������������Ȩ�� �����°��浣�ΰ��� ");
		fame_title.setText(":: "+username+" ������ ::");
		fame2_title.setText("��ʾ ��̳/ĳ����/ĳ�û� �� "+username+" �Ĺ�ϵ");
		search_title.setText(":: "+username+" ����������  ::");
		topic_button.setText("[���� "+username+" ����������]");
		reply_button.setText("[���� "+username+" �����Ļظ�]");
		user_id.setText(ret.get_uid());
		user_name.setText(username);
		if(ret.get_hasemail()){
			user_email.setText(ret.get_email());
		}else{
			user_email.setVisibility(View.GONE);
			user_email_title.setVisibility(View.GONE);
		}
		if(ret.get_hastel()){
			user_tel.setText(ret.get_tel());
		}else{
			user_tel.setVisibility(View.GONE);
			user_tel_title.setVisibility(View.GONE);
		}
		user_group.setText(ret.get_group());
		user_posttotal.setText(ret.get_posts());
		if(ret.get_money().equals("0")){
			user_money_gold.setVisibility(View.GONE);
			avatargold.setVisibility(View.GONE);
			user_money_silver.setVisibility(View.GONE);
			avatarsilver.setVisibility(View.GONE);
			user_money_copper.setText("0");
		}else{
			int moneytotal=Integer.parseInt(ret.get_money());
			int moneygold=(int)moneytotal/10000;
			int moneysilver = (int) (moneytotal-moneygold*10000)/100;
			int moneycopper= (int) (moneytotal-moneygold*10000-moneysilver*100);
			if(moneygold>0){
				user_money_gold.setText(String.valueOf(moneygold));
				user_money_silver.setText(String.valueOf(moneysilver));
				user_money_copper.setText(String.valueOf(moneycopper));
			}else{
				if(moneysilver>0){
					user_money_gold.setVisibility(View.GONE);
					avatargold.setVisibility(View.GONE);
					user_money_silver.setText(String.valueOf(moneysilver));
					user_money_copper.setText(String.valueOf(moneycopper));
				}else{
					user_money_gold.setVisibility(View.GONE);
					avatargold.setVisibility(View.GONE);
					user_money_silver.setVisibility(View.GONE);
					avatarsilver.setVisibility(View.GONE);
					user_money_copper.setText(String.valueOf(moneycopper));
				}
			}
		}
		int verified=Integer.parseInt(ret.get_verified());
		if(verified>0){
			if(ret.get_muteTime().equals("-1")){
				user_shutup_title.setVisibility(View.GONE);
				user_shutup.setVisibility(View.GONE);
				user_state.setText("�Ѽ���");
				user_state.setTextColor(this.getResources().getColor(R.color.activecolor));
			}else{
				user_shutup.setText(ret.get_muteTime());
				user_shutup.setTextColor(this.getResources().getColor(R.color.mutedcolor));
				user_state.setText("�ѽ���");
				user_state.setTextColor(this.getResources().getColor(R.color.mutedcolor));
			}
		}else if(verified==0){
			user_state.setText("δ����(?)");
			user_state.setTextColor(this.getResources().getColor(R.color.unactivecolor));
			user_shutup_title.setVisibility(View.GONE);
			user_shutup.setVisibility(View.GONE);
		}else if(verified==-1){
			user_state.setText("NUKED(?)");
			user_state.setTextColor(this.getResources().getColor(R.color.nukedcolor));
			if(ret.get_muteTime().equals("-1")){
				user_shutup_title.setVisibility(View.GONE);
				user_shutup.setVisibility(View.GONE);
			}else{
				user_shutup.setText(ret.get_muteTime());
				user_shutup.setTextColor(this.getResources().getColor(R.color.mutedcolor));
			}
		}else{
			user_state.setText("�ѽ���");
			user_state.setTextColor(this.getResources().getColor(R.color.mutedcolor));
			if(ret.get_muteTime().equals("-1")){
				user_shutup_title.setVisibility(View.GONE);
				user_shutup.setVisibility(View.GONE);
			}else{
				user_shutup.setText(ret.get_muteTime());
				user_shutup.setTextColor(this.getResources().getColor(R.color.mutedcolor));
			}
		}
		user_title.setText(ret.get_title());
		user_registertime.setText(ret.get_regdate());
		user_lastlogintime.setText(ret.get_lastpost());
		handleAvatar(avatarImage, ret);
		handleSignWebview(signwebview,ret);
		handleadminWebview(adminwebview,ret);
		handlefameWebview(famewebview,ret);
		topic_button.setOnClickListener(new OnClickListener(){

			Intent intent_search = new Intent(view.getContext(), PhoneConfiguration.getInstance().topicActivityClass);
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent_search.putExtra("fid","-7");
	    		intent_search.putExtra("author", ret.get_username());
	    		intent_search.putExtra("authorid", ret.get_uid());
	    		startActivity(intent_search);
			}
			
		});

		reply_button.setOnClickListener(new OnClickListener(){

			Intent intent_search = new Intent(view.getContext(), PhoneConfiguration.getInstance().topicActivityClass);
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				intent_search.putExtra("fid","-7");
	    		intent_search.putExtra("author", ret.get_username()+"&searchpost=1");
	    		intent_search.putExtra("authorid", ret.get_uid());
	    		startActivity(intent_search);
			}
			
		});
		view.setVisibility(View.VISIBLE);
	}
	
	private String createHTMLofadmin(ProfileData ret){
		int i;
		String rest="";
		List<adminForumsData> adminForumsEntryList=ret.get_adminForumsEntryList();
		for(i=0;i<ret.get_adminForumsEntryListrows();i++){
			rest+="<a style=\"color:#551200;\" href=\"http://nga.178.com/thread.php?fid="+adminForumsEntryList.get(i).get_fid()+"\">["+adminForumsEntryList.get(i).get_fname()+"]</a>&nbsp;";
		}
		if(rest==""){
			return "�޹������";
		}else{
			return rest+"<br>";
		}
	}
	
	private String createHTMLoffame(ProfileData ret){
		int i;
		String rest="<ul style=\"padding: 0px; margin: 0px;\">";
		String fame = ret.get_fame();
		double  famenum = (double) Double.parseDouble(fame)/10;
		rest+="<li style=\"display: block;float: left;width: 33%;\">"
				+"<label style=\"float: left;color: #121C46;\">����</label>"
				+"<span style=\"float: left; color: #808080;\">:</span>"
				+"<span style=\"float: left; color: #808080;\">"+Double.toString(famenum)+"</span></li>";
		List<ReputationData> ReputationEntryList=ret.get_ReputationEntryList();
		for(i=0;i<ret.get_ReputationEntryListrows();i++){
			rest+="<li style=\"display: block;float: left;width: 33%;\">"
					+"<label style=\"float: left;color: #121C46;\">"+ReputationEntryList.get(i).get_name()+"</label>"
					+"<span style=\"float: left; color: #808080;\">:</span>"
					+"<span style=\"float: left; color: #808080;\">"+ReputationEntryList.get(i).get_data()+"</span></li>";
		}
		return rest+"</ul><br>";
	}
	
	private void handleSignWebview(WebView contentTV,ProfileData ret){
		ThemeManager theme = ThemeManager.getInstance();
		int bgColor = getResources().getColor(R.color.profilebgcolor);
		int fgColor = getResources().getColor(theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x",htmlfgColor);
		
		
	    WebViewClient client = new ArticleListWebClient(this);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(null, signatureToHtmlText(ret,showImage,showImageQuality(isInWifi()),fgColorStr,bgcolorStr),
				"text/html", "utf-8", null);
	}


	public static int showImageQuality(boolean isInWifi) {
		if (isInWifi)
		{
			return 0;
		}
		else
		{
			return PhoneConfiguration.getInstance().imageQuality;
		}
	}

	private void handleadminWebview(WebView contentTV,ProfileData ret){
		int bgColor = getResources().getColor(R.color.profilebgcolor);
		int fgColor = getResources().getColor(R.color.profilefcolor);
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x",htmlfgColor);
		
		
	    WebViewClient client = new ArticleListWebClient(this);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(null, adminToHtmlText(ret,showImage,showImageQuality(isInWifi()),fgColorStr,bgcolorStr),
				"text/html", "utf-8", null);
	}

	private void handlefameWebview(WebView contentTV,ProfileData ret){
		int bgColor = getResources().getColor(R.color.profilebgcolor);
		int fgColor = getResources().getColor(R.color.profilefcolor);
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x",bgColor);
		
		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x",htmlfgColor);
		
		
	    WebViewClient client = new ArticleListWebClient(this);
		contentTV.setBackgroundColor(0);
		contentTV.setFocusableInTouchMode(false);
		contentTV.setFocusable(false);
		if (ActivityUtil.isGreaterThan_2_2()) {
			contentTV.setLongClickable(false);
		}
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
		WebSettings setting = contentTV.getSettings();
		setting.setDefaultFontSize(PhoneConfiguration.getInstance()
				.getWebSize());
		setting.setJavaScriptEnabled(false);
		contentTV.setWebViewClient(client);
		contentTV.loadDataWithBaseURL(null, fameToHtmlText(ret,showImage,showImageQuality(isInWifi()),fgColorStr,bgcolorStr),
				"text/html", "utf-8", null);
	}

	public String fameToHtmlText(final ProfileData ret,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = createHTMLoffame(ret);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr
				+ "' size='2'>"
				+ ngaHtml
				+ "</font></body>";

		return ngaHtml;
	}

	public String adminToHtmlText(final ProfileData ret,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = createHTMLofadmin(ret);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr
				+ "' size='2'>"
				+ ngaHtml
				+ "</font></body>";

		return ngaHtml;
	}
	
	
	
	public String signatureToHtmlText(final ProfileData ret,
			boolean showImage, int imageQuality, final String fgColorStr,
			final String bgcolorStr) {
		HashSet<String> imageURLSet = new HashSet<String>();
		String ngaHtml = StringUtil.decodeForumTag(ret.get_sign(), showImage,
				imageQuality, imageURLSet);
		if (imageURLSet.size() == 0) {
			imageURLSet = null;
		}
		ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
				+ "<body bgcolor= '#"
				+ bgcolorStr
				+ "'>"
				+ "<font color='#"
				+ fgColorStr
				+ "' size='2'>"
				+"<div style=\"border: 3px solid rgb(204, 204, 204);padding: 2px; \">"
				+ ngaHtml+
				"</div>"
				+ "</font></body>";

		return ngaHtml;
	}
	
	
	private Bitmap defaultAvatar = null;
	private void handleAvatar(ImageView avatarIV, ProfileData row) {

		final String avatarUrl = parseAvatarUrl(row.get_avatar());//
		final String userId = String.valueOf(row.get_uid());
		if (PhoneConfiguration.getInstance().nikeWidth < 3) {
			avatarIV.setImageBitmap(null);
			return;
		}
		if (defaultAvatar == null
				|| defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
			Resources res = avatarIV.getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}

		Object tagObj = avatarIV.getTag();
		if (tagObj instanceof AvatarTag) {
			AvatarTag origTag = (AvatarTag) tagObj;
			if (origTag.isDefault == false) {
				ImageUtil.recycleImageView(avatarIV);
				// Log.d(TAG, "recycle avatar:" + origTag.lou);
			} else {
				// Log.d(TAG, "default avatar, skip recycle");
			}
		}
		AvatarTag tag = new AvatarTag(0, true);
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(tag);
		if (!StringUtil.isEmpty(avatarUrl)) {
			final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
			if (avatarPath != null) {
				File f = new File(avatarPath);
				if (f.exists() && !isPending(avatarUrl)) {

					Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
					if (bitmap != null) {
						avatarIV.setImageBitmap(bitmap);
						tag.isDefault = false;
					} else
						f.delete();
					long date = f.lastModified();
					if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
						f.delete();
					}

				} else {
					final boolean downImg = isInWifi()
							|| PhoneConfiguration.getInstance()
									.isDownAvatarNoWifi();

					new AvatarLoadTask(avatarIV, null, downImg, 0, this)
							.execute(avatarUrl, avatarPath, userId);

				}
			}
		}

	}

	private boolean isPending(String url) {
		boolean ret = false;
		synchronized (lock) {
			ret = urlSet.contains(url);
		}
		return ret;
	}
	private static String parseAvatarUrl(String js_escap_avatar) {
		// "js_escap_avatar":"{ \"t\":1,\"l\":2,\"0\":{ \"0\":\"http://pic2.178.com/53/533387/month_1109/93ba4788cc8c7d6c75453fa8a74f3da6.jpg\",\"cX\":0.47,\"cY\":0.78},\"1\":{ \"0\":\"http://pic2.178.com/53/533387/month_1108/8851abc8674af3adc622a8edff731213.jpg\",\"cX\":0.49,\"cY\":0.68}}"
		if (null == js_escap_avatar)
			return null;

		int start = js_escap_avatar.indexOf("http");
		if (start == 0 || start == -1)
			return js_escap_avatar;
		int end = js_escap_avatar.indexOf("\"", start);//
		if (end == -1)
			end = js_escap_avatar.length();
		String ret = null;
		try {
			ret = js_escap_avatar.substring(start, end);
		} catch (Exception e) {
			Log.e(TAG, "cann't handle avatar url " + js_escap_avatar);
		}
		return ret;
	}

	private boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}
	
	@TargetApi(11)
	private void activeActionMode(){
		mActionModeCallback = new ActionMode.Callback() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				onContextItemSelected(item);
				mode.finish();
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				//int position = listview.getCheckedItemPosition();
				//listview.setItemChecked(position, false);
				
			}

			@Override
			public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}
	
	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(view);
		}
		super.onResume();
	}

	@Override
	public void jsonfinishLoad(ProfileData result) {
		// TODO Auto-generated method stub
		if(result!=null){
			writetopage(result);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		default:
			finish();
		}
		return true;
	}

	@Override
	public void OnAvatarLoadStart(String url) {
		synchronized (lock) {
			this.urlSet.add(url);
		}

	}

	@Override
	public void OnAvatarLoadComplete(String url) {
		synchronized (lock) {
			this.urlSet.remove(url);
		}

	}
}