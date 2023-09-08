# Simple SMS Messenger
TODO:

https://github.com/SimpleMobileTools/Simple-SMS-Messenger

[+] - select language for each sender (orig, target)
[+] - select language for each sender, remove "---" use "original" instead
[+] - select target language "original" to return to original lang
[] - select default language for all NEW senders except ones from first list (orig, target)
[] - and set the language by long-tap on the chat for all selected
[+-] - option to translate after receiving, manually (button (orig, target) ) // add screen refresh
[+] - save sms with their original text and their translation
[+] - select multiply
[+] - show translated in preview sms list
[+] - add Ukrainian

[+] - check the internet, if not - write errors
[] - disable/enable auto translation

issues
[] - in first run i guess all messages in SMS db treated as new? and writing to APP db is is normal?

[rejected] - fix Google title (MMS sender_name = sender_phone_number =  google@rbm.goog, read from sms, save to db, read from dg OK, display - bug)

long string read by, there is nothing we can do
Context.kt
fun Context.getPhoneNumberFromAddressId(canonicalAddressId: Int): String {
val aaa = cursor.getStringValue(Mms.Addr.ADDRESS)

short string read by, google@rbm.goog
Context.kt
funContext.getMMS(threadId: Long? = null, getImageResolutions: Boolean = false, sortOrder: String? = null, dateFrom: Int = -1): ArrayList<Message> {
senderNumber = getMMSSender(mmsId)
senderName = namePhoto.name
getNameAndPhotoFromPhoneNumber(getMMSSender(mmsId)).name google@rbm.goog
Context.getMMSSender



table messages
phoneNumber sender_phone_number
google@rbm.goog
title sender_name
google@rbm.goog

getNewConversations
getConversations(privateContacts = MyContactsContentProvider.getSimpleContacts(this, privateCursor) ).map { it.title }

table conversations
phoneNumber sender_phone_number
m5xw6z3mmvpv6qkul5pxeytnfztw633hl45v6r3pn5twyzk7hnpsgmbqgaydama=@bot.rcs.google.com
title sender_name
m5xw6z3mmvpv6qkul5pxeytnfztw633hl45v6r3pn5twyzk7hnpsgmbqgaydama=@bot.rcs.google.com


BaseConversationsAdapter.kt
onBindViewHolder
getItem(position)//it`s getting a list somewhere
from BaseConversationsAdapter.kt updateConversations submitList?


conversation.title
phoneNumber
m5xw6z3mmvpv6qkul5pxeytnfztw633hl45v6r3pn5twyzk7hnpsgmbqgaydama=@bot.rcs.google.com
title
m5xw6z3mmvpv6qkul5pxeytnfztw633hl45v6r3pn5twyzk7hnpsgmbqgaydama=@bot.rcs.google.com



<img alt="Logo" src="graphics/icon.png" width="120" />

A great way to stay in touch with your relatives, by sending both SMS and MMS messages. The app properly handles group messaging too, just like blocking numbers from Android 7+. Keep in touch with all of your contacts using the messaging app on your phone. It's never been easier to share photos, send emojis, or just say a quick hello. There's so much you can do with your messages, like mute conversations or assign special message tones for certain contacts. With this text message and group messaging app, you can enjoy the daily private messaging and group messaging in a more fun way.

It offers many date formats to choose from, to make you feel comfortable at using it. You can toggle between 12 and 24 hours time format too. This app also gives you the flexibility of sms backup. This way, you don't have to save the messages on any external device or use any other hardware to save it. This sms backup feature will help you efficiently save text message and mms data without being a burden on internal storage.

This messaging app has a really tiny app size compared to the competition, making it really fast to download. The sms backup technique is helpful when you have to change your device or it gets stolen. This way, you can retrieve the text message from both group messaging and private messaging easily using the sms backup in this messaging app.

The blocking feature helps preventing unwanted messages easily, you can block all messages from not stored contacts too. Blocked numbers can be both exported and imported for easy backup. All conversations can be easily exported to a file for simple backup too or migrating between devices.

You can customize which part of the message is visible on the lockscreen too. You can choose if you want only the sender shown, the message, or nothing for enhanced privacy.

It comes with material design and dark theme by default, provides great user experience for easy usage. This messaging app also provides users with the ability to search messages quickly and efficiently. Gone are the days when you have to scroll down through all the private messaging and group messaging conversations to reach your required message. Simply search and get what you want with this text messaging app.

Contains no ads or unnecessary permissions. It is fully opensource, provides customizable colors. You can also customize the font of your text message in both group messaging and private messaging.

Check out the full suite of Simple Tools here:  
https://www.simplemobiletools.com

Facebook:  
https://www.facebook.com/simplemobiletools

Reddit:  
https://www.reddit.com/r/SimpleMobileTools

Telegram:  
https://t.me/SimpleMobileTools

<a href='https://play.google.com/store/apps/details?id=com.simplemobiletools.smsmessenger'><img src='https://simplemobiletools.com/images/button-google-play.svg' alt='Get it on Google Play' height=45/></a>
<a href='https://f-droid.org/packages/com.simplemobiletools.smsmessenger'><img src='https://simplemobiletools.com/images/button-f-droid.png' alt='Get it on F-Droid' height='45' /></a>

<div style="display:flex;">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.jpeg" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.jpeg" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.jpeg" width="30%">
</div>

