package com.excelliance.open;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.view.Window;

/**
 *
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts wich allow you to
 * create and use your own look & feel.
 *
 */
public class CustomDialog extends Dialog {
    TextView mTextViewMessage = null;
    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
    }

    public void setMessage(String message) {
        this.mTextViewMessage.setText(message);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
		private Context topActivity;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;

        private CheckBox checkBox;
        private String checkBoxText;
        private boolean checked;
        private OnCheckedChangeListener checkBoxListener;

        private OnClickListener
        positiveButtonClickListener,
        negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

        public Builder(Context context, Context topActivity) {
            this.context = context;
            this.topActivity = topActivity;
        }

        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                                      .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                                      .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setCheckBox(String checkBoxText,boolean checked,
                                   OnCheckedChangeListener listener) {
            this.checkBoxText = checkBoxText;
            this.checked = checked;
            this.checkBoxListener = listener;
            return this;
        }
        public boolean getChecked() {
            if(this.checkBox != null) {
                return this.checkBox.isChecked();
            } else {
                return false;
            }
        }

        /**
         * Create the custom dialog
         */
        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            String packageName = context.getPackageName();
            int resId = context.getResources().getIdentifier("lebian_dialog_theme", "style", packageName);
            final CustomDialog dialog = (topActivity == null) ? new CustomDialog(context, resId) : new CustomDialog(topActivity);
			dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            resId = context.getResources().getIdentifier("lebian_dialog", "layout", packageName);
            View layout = inflater.inflate(resId, null);
            dialog.addContentView(layout, new LayoutParams(
                                      LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            resId = context.getResources().getIdentifier("lebian_title", "id", packageName);
            ((TextView) layout.findViewById(resId)).setText(title);
            // set the confirm button
            resId = context.getResources().getIdentifier("lebian_positiveButton", "id", packageName);
            if (positiveButtonClickListener != null) {
                View v = layout.findViewById(resId);
                if (positiveButtonText != null) {
                    if ( v instanceof Button){
                        ((Button) layout.findViewById(resId)).setText(positiveButtonText);
                    }
                    else if( v instanceof TextView){
                        ((TextView) layout.findViewById(resId)).setText(positiveButtonText);
                    }
                }
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        positiveButtonClickListener.onClick(
                            dialog,
                            DialogInterface.BUTTON_POSITIVE);
                    }
                });
                
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(resId).setVisibility(
                    View.GONE);
            }
            // set the cancel button
            resId = context.getResources().getIdentifier("lebian_negativeButton", "id", packageName);
            if (negativeButtonClickListener != null) {
                View v = layout.findViewById(resId);
                if (negativeButtonText != null) {
                    if ( v instanceof Button){
                        ((Button) layout.findViewById(resId)).setText(negativeButtonText);
                    }
                    else if( v instanceof TextView){
                        ((TextView) layout.findViewById(resId)).setText(negativeButtonText);
                    }
                }
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        negativeButtonClickListener.onClick(
                            dialog,
                            DialogInterface.BUTTON_NEGATIVE);
                    }
                });
                
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(resId).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                resId = context.getResources().getIdentifier("lebian_message", "id", packageName);
                dialog.mTextViewMessage = (TextView)layout.findViewById(resId);
                dialog.mTextViewMessage.setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                resId = context.getResources().getIdentifier("lebian_content", "id", packageName);
                ((LinearLayout) layout.findViewById(resId))
                .removeAllViews();
                ((LinearLayout) layout.findViewById(resId))
                .addView(contentView,
                         new LayoutParams(
                             LayoutParams.WRAP_CONTENT,
                             LayoutParams.WRAP_CONTENT));
            }

            if(checkBoxText != null) {
                resId = context.getResources().getIdentifier("lebian_check_box_layout", "id", packageName);
                layout.findViewById(resId).setVisibility(View.VISIBLE);
                resId = context.getResources().getIdentifier("lebian_check_box", "id", packageName);
                checkBox = (CheckBox)layout.findViewById(resId);
                if(checkBoxListener != null) {
                    checkBox.setOnCheckedChangeListener(checkBoxListener);
                }
                checkBox.setChecked(this.checked);
            }

            dialog.setContentView(layout);
            return dialog;
        }

    }

}


