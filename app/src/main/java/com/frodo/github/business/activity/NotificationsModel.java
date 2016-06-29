package com.frodo.github.business.activity;

import com.fasterxml.jackson.core.type.TypeReference;
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

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

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
		return Observable.create(new Observable.OnSubscribe<Response>() {
			@Override
			public void call(Subscriber<? super Response> subscriber) {
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(Path.Activity.NOTIFICATIONS)
						.build();
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				fetchReceivedEventsNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
				getMainController().getBackgroundExecutor().execute(fetchReceivedEventsNetworkDataTask);
			}
		}).flatMap(new Func1<Response, Observable<List<Notification>>>() {
			@Override
			public Observable<List<Notification>> call(Response response) {
				ResponseBody rb = (ResponseBody) response.getBody();
				try {
					List<Notification> notifications = JsonConverter.convert(rb.string(), new TypeReference<List<Notification>>() {
					});
					return Observable.just(notifications);
				} catch (IOException e) {
					return Observable.error(e);
				}
			}
		});
	}


}
