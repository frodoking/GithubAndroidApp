package com.frodo.github.business.activity;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.response.Notification;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by frodo on 16/6/10.
 */
public class NotificationsModel extends AbstractModel {

	public static final String TAG = NotificationsModel.class.getSimpleName();
	private AndroidFetchNetworkDataTask fetchReceivedEventsNetworkDataTask;

	public NotificationsModel(MainController controller) {
		super(controller);
	}

	public Observable<List<Notification>> loadNotifications() {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(Path.Activity.NOTIFICATIONS)
						.build();
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				fetchReceivedEventsNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(fetchReceivedEventsNetworkDataTask);
			}
		}).flatMap(new Function<Response, ObservableSource<List<Notification>>>() {
			@Override public ObservableSource<List<Notification>> apply(Response response)
			{
				ResponseBody rb = (ResponseBody) response.getBody();
				try {
					List<Notification> notifications = JsonConverter.convert(rb.string(), new TypeToken<List<Notification>>() {
					}.getType());
					return Observable.just(notifications);
				} catch (IOException e) {
					return Observable.error(e);
				}
			}
		});
	}


}
