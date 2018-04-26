package com.xulan.demo.view;

import com.xulan.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class CustomPopDialog extends Dialog {
	public CustomPopDialog(Context context) {
		super(context);
	}

	public CustomPopDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private Bitmap image;

		public Builder(Context context) {
			this.context = context;
		}

		public Bitmap getImage() {
			return image;
		}

		public void setImage(Bitmap image) {
			this.image = image;
		}

		public CustomPopDialog create() {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomPopDialog dialog = new CustomPopDialog(context, R.style.codeDialog);
			View layout = inflater.inflate(R.layout.dialog_share_qrcode, null);
			dialog.addContentView(layout, new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			dialog.setContentView(layout);
			ImageView img = (ImageView) layout.findViewById(R.id.img_qrcode);
			img.setImageBitmap(getImage());
			return dialog;
		}
	}
}
