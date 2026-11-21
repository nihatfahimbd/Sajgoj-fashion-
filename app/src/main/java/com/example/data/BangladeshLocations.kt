package com.example.data

data class BangladeshDistrict(
  val nameEn: String,
  val nameBn: String,
  val division: String,
  val thanas: List<String>
) {
  val displayLabel: String get() = "$nameBn ($nameEn)"
}

object BangladeshLocations {
  val districts: List<BangladeshDistrict> = listOf(
    // Dhaka Division (13)
    BangladeshDistrict(
      "Dhaka", "ঢাকা", "Dhaka",
      listOf("ধানমন্ডি", "গুলশান", "বনানী", "উত্তরা পূর্ব", "উত্তরা পশ্চিম", "মিরপুর", "পল্লবী", "মোহাম্মদপুর", "তেজগাঁও", "বাড্ডা", "রামপুরা", "খিলগাঁও", "মতিঝিল", "রমনা", "লালবাগ", "কোতোয়ালি", "যাত্রাবাড়ী", "ডেমরা", "শ্যামপুর", "হাজারীবাগ", "সাভার", "ধামরাই", "কেরানীগঞ্জ", "দোহার", "নবাবগঞ্জ")
    ),
    BangladeshDistrict(
      "Gazipur", "গাজীপুর", "Dhaka",
      listOf("গাজীপুর সদর", "টঙ্গী পূর্ব", "টঙ্গী পশ্চিম", "কালিয়াকৈর", "কালীগঞ্জ", "কাপাসিয়া", "শ্রীপুর")
    ),
    BangladeshDistrict(
      "Narayanganj", "নারায়ণগঞ্জ", "Dhaka",
      listOf("নারায়ণগঞ্জ সদর", "ফতুল্লা", "সিদ্ধিরগঞ্জ", "বন্দর", "রূপগঞ্জ", "সোনারগাঁও", "আড়াইহাজার")
    ),
    BangladeshDistrict(
      "Narsingdi", "নরসিংদী", "Dhaka",
      listOf("নরসিংদী সদর", "পলাশ", "শিবপুর", "বেলাবো", "মনোহরদী", "রায়পুরা", "মাধবদী")
    ),
    BangladeshDistrict(
      "Manikganj", "মানিকগঞ্জ", "Dhaka",
      listOf("মানিকগঞ্জ সদর", "সিংগাইর", "সাটুরিয়া", "ঘিওর", "শিবালয়", "দৌলতপুর", "হরিরামপুর")
    ),
    BangladeshDistrict(
      "Munshiganj", "মুন্সীগঞ্জ", "Dhaka",
      listOf("মুন্সীগঞ্জ সদর", "টঙ্গীবাড়ী", "সিরাজদিখান", "লৌহজং", "শ্রীনগর", "গজারিয়া")
    ),
    BangladeshDistrict(
      "Tangail", "টাঙ্গাইল", "Dhaka",
      listOf("টাঙ্গাইল সদর", "মির্জাপুর", "কালিহাতী", "ঘাটাইল", "সখিপুর", "বাসাইল", "মধুপুর", "ধনবাড়ী", "গোপালপুর", "ভূঞাপুর", "নাগরপুর", "দেলদুয়ার")
    ),
    BangladeshDistrict(
      "Kishoreganj", "কিশোরগঞ্জ", "Dhaka",
      listOf("কিশোরগঞ্জ সদর", "ভৈরব", "বাজিতপুর", "কুলিয়ারচর", "কটিয়াদী", "পাকুন্দিয়া", "হোসেনপুর", "করিমগঞ্জ", "তাড়াইল", "ইটনা", "মিঠামইন", "অষ্টগ্রাম", "নিকলী")
    ),
    BangladeshDistrict(
      "Faridpur", "ফরিদপুর", "Dhaka",
      listOf("ফরিদপুর সদর", "বোয়ালমারী", "ভাঙ্গা", "নগরকান্দা", "সালথা", "সদরপুর", "চরভদ্রাসন", "মধুখালী", "আলফাডাঙ্গা")
    ),
    BangladeshDistrict(
      "Gopalganj", "গোপালগঞ্জ", "Dhaka",
      listOf("গোপালগঞ্জ সদর", "টুঙ্গিপাড়া", "কোটালীপাড়া", "কাশিয়ানী", "মুকসুদপুর")
    ),
    BangladeshDistrict(
      "Madaripur", "মাদারীপুর", "Dhaka",
      listOf("মাদারীপুর সদর", "শিবচর", "কালকিনি", "রাজৈর", "ডাসার")
    ),
    BangladeshDistrict(
      "Rajbari", "রাজবাড়ী", "Dhaka",
      listOf("রাজবাড়ী সদর", "গোয়ালন্দ", "পাংশা", "বালিয়াকান্দি", "কালুখালী")
    ),
    BangladeshDistrict(
      "Shariatpur", "শরীয়তপুর", "Dhaka",
      listOf("শরীয়তপুর সদর", "পালং", "জাজিরা", "নড়িয়া", "ভেদরগঞ্জ", "ডামুড্যা", "গোসাইরহাট")
    ),

    // Chattogram Division (11 — includes Chandpur!)
    BangladeshDistrict(
      "Chandpur", "চাঁদপুর", "Chattogram",
      listOf("চাঁদপুর সদর", "হাজীগঞ্জ", "শাহরাস্তি", "কচুয়া", "মতলব দক্ষিণ", "মতলব উত্তর", "ফরিদগঞ্জ", "হাইমচর")
    ),
    BangladeshDistrict(
      "Chattogram", "চট্টগ্রাম", "Chattogram",
      listOf("কোতোয়ালি", "পাঁচলাইশ", "ডবলমুরিং", "হালিশহর", "পাহাড়তলী", "খুলশী", "চান্দগাঁও", "বায়েজিদ বোস্তামী", "পতেঙ্গা", "বন্দর", "সীতাকুণ্ড", "মিরসরাই", "হাটহাজারী", "রাউজান", "রাঙ্গুনিয়া", "পটিয়া", "বোয়ালখালী", "আনোয়ারা", "চন্দনাইশ", "সাতকানিয়া", "লোহাগাড়া", "বাঁশখালী", "ফটিকছড়ি", "সন্দ্বীপ", "কর্ণফুলী")
    ),
    BangladeshDistrict(
      "Cumilla", "কুমিল্লা", "Chattogram",
      listOf("কুমিল্লা আদর্শ সদর", "সদর দক্ষিণ", "দাউদকান্দি", "চান্দিনা", "দেবিদ্বার", "মুরাদনগর", "বুড়িচং", "ব্রাহ্মণপাড়া", "হোমনা", "মেঘনা", "তিতাস", "লাকসাম", "মনোহরগঞ্জ", "চৌদ্দগ্রাম", "নাঙ্গলকোট", "বরুড়া", "লালমাই")
    ),
    BangladeshDistrict(
      "Cox's Bazar", "কক্সবাজার", "Chattogram",
      listOf("কক্সবাজার সদর", "রামু", "উখিয়া", "টেকনাফ", "চকরিয়া", "পেকুয়া", "মহেশখালী", "কুতুবদিয়া", "ঈদগাঁও")
    ),
    BangladeshDistrict(
      "Brahmanbaria", "ব্রাহ্মণবাড়িয়া", "Chattogram",
      listOf("ব্রাহ্মণবাড়িয়া সদর", "আশুগঞ্জ", "সরাইল", "নাসিরনগর", "নবীনগর", "কসবা", "আখাউড়া", "বাঞ্ছারামপুর", "বিজয়নগর")
    ),
    BangladeshDistrict(
      "Noakhali", "নোয়াখালী", "Chattogram",
      listOf("নোয়াখালী সদর (সুধারাম)", "বেগমগঞ্জ", "সোনাইমুড়ী", "চাটখিল", "সেনবাগ", "কোম্পানীগঞ্জ", "কবিরহাট", "সুবর্ণচর", "হাতিয়া")
    ),
    BangladeshDistrict(
      "Feni", "ফেনী", "Chattogram",
      listOf("ফেনী সদর", "দাগনভূঞা", "সোনাগাজী", "ছাগলনাইয়া", "ফুলগাজী", "পরশুরাম")
    ),
    BangladeshDistrict(
      "Lakshmipur", "লক্ষ্মীপুর", "Chattogram",
      listOf("লক্ষ্মীপুর সদর", "রায়পুর", "রামগঞ্জ", "রামগতি", "কমলনগর")
    ),
    BangladeshDistrict(
      "Rangamati", "রাঙ্গামাটি", "Chattogram",
      listOf("রাঙ্গামাটি সদর (কোতোয়ালি)", "কাপ্তাই", "কাউখালী", "নানিয়ারচর", "লংগদু", "বাঘাইছড়ি", "বরকল", "জুরাছড়ি", "বিলাইছড়ি", "রাজস্থলী")
    ),
    BangladeshDistrict(
      "Khagrachhari", "খাগড়াছড়ি", "Chattogram",
      listOf("খাগড়াছড়ি সদর", "দীঘিনালা", "পানছড়ি", "মাটিরাঙ্গা", "রামগড়", "মানিকছড়ি", "লক্ষ্মীছড়ি", "মহালছড়ি", "গুইমারা")
    ),
    BangladeshDistrict(
      "Bandarban", "বান্দরবান", "Chattogram",
      listOf("বান্দরবান সদর", "লামা", "আলীকদম", "নাইক্ষ্যংছড়ি", "রোয়াংছড়ি", "রুমা", "থানচি")
    ),

    // Rajshahi Division (8)
    BangladeshDistrict(
      "Rajshahi", "রাজশাহী", "Rajshahi",
      listOf("বোয়ালিয়া", "মতিহার", "রাজপাড়া", "শাহমখদুম", "পবা", "মোহনপুর", "বাগমারা", "দুর্গাপুর", "পুঠিয়া", "চারঘাট", "বাঘা", "তানোর", "গোদাগাড়ী")
    ),
    BangladeshDistrict(
      "Bogura", "বগুড়া", "Rajshahi",
      listOf("বগুড়া সদর", "শেরপুর", "শাজাহানপুর", "কাহালু", "দুপচাঁচিয়া", "আদমদীঘি", "নন্দীগ্রাম", "শিবগঞ্জ", "সোনাতলা", "গাবতলী", "সারিয়াকান্দি", "ধুনট")
    ),
    BangladeshDistrict(
      "Pabna", "পাবনা", "Rajshahi",
      listOf("পাবনা সদর", "ঈশ্বরদী", "সাঁথিয়া", "বেড়া", "সুজানগর", "আটঘরিয়া", "চাটমোহর", "ভাঙ্গুড়া", "ফরিদপুর")
    ),
    BangladeshDistrict(
      "Sirajganj", "সিরাজগঞ্জ", "Rajshahi",
      listOf("সিরাজগঞ্জ সদর", "কাজিপুর", "রায়গঞ্জ", "তাড়াশ", "উল্লাপাড়া", "শাহজাদপুর", "বেলকুচি", "কামারখন্দ", "চৌহালী")
    ),
    BangladeshDistrict(
      "Natore", "নাটোর", "Rajshahi",
      listOf("নাটোর সদর", "সিংড়া", "গুরুদাসপুর", "বড়াইগ্রাম", "লালপুর", "বাগাতিপাড়া", "নলডাঙ্গা")
    ),
    BangladeshDistrict(
      "Naogaon", "নওগাঁ", "Rajshahi",
      listOf("নওগাঁ সদর", "রানীনগর", "আত্রাই", "মান্দা", "মহাদেবপুর", "বদলগাছী", "পত্নীতলা", "ধামইরহাট", "সাপাহার", "পোরশা", "নিয়ামতপুর")
    ),
    BangladeshDistrict(
      "Chapainawabganj", "চাঁপাইনবাবগঞ্জ", "Rajshahi",
      listOf("চাঁপাইনবাবগঞ্জ সদর", "শিবগঞ্জ", "গোমস্তাপুর", "নাচোল", "ভোলাহাট")
    ),
    BangladeshDistrict(
      "Joypurhat", "জয়পুরহাট", "Rajshahi",
      listOf("জয়পুরহাট সদর", "পাঁচবিবি", "আক্কেলপুর", "কালাই", "ক্ষেতলাল")
    ),

    // Khulna Division (10)
    BangladeshDistrict(
      "Khulna", "খুলনা", "Khulna",
      listOf("খুলনা সদর", "সোনাডাঙ্গা", "খালিশপুর", "দৌলতপুর", "খানজাহান আলী", "ডুমুরিয়া", "বটিয়াঘাটা", "রূপসা", "তেরখাদা", "দিঘলিয়া", "ফুলতলা", "পাইকগাছা", "কয়রা", "দাকোপ")
    ),
    BangladeshDistrict(
      "Jashore", "যশোর", "Khulna",
      listOf("যশোর সদর (কোতোয়ালি)", "অভয়নগর", "বাঘারপাড়া", "চৌগাছা", "ঝিকরগাছা", "শার্শা", "কেশবপুর", "মনিরামপুর")
    ),
    BangladeshDistrict(
      "Satkhira", "সাতক্ষীরা", "Khulna",
      listOf("সাতক্ষীরা সদর", "তালা", "কলারোয়া", "দেবহাটা", "আশাশুনি", "কালিগঞ্জ", "শ্যামনগর")
    ),
    BangladeshDistrict(
      "Bagerhat", "বাগেরহাট", "Khulna",
      listOf("বাগেরহাট সদর", "ফকিরহাট", "মোল্লাহাট", "চিতলমারী", "কচুয়া", "রামপাল", "মোংলা", "মোড়েলগঞ্জ", "শরণখোলা")
    ),
    BangladeshDistrict(
      "Kushtia", "কুষ্টিয়া", "Khulna",
      listOf("কুষ্টিয়া সদর", "কুমারখালী", "খোকসা", "মিরপুর", "ভেড়ামারা", "দৌলতপুর")
    ),
    BangladeshDistrict(
      "Jhenaidah", "ঝিনাইদহ", "Khulna",
      listOf("ঝিনাইদহ সদর", "শৈলকুপা", "হরিণাকুণ্ডু", "কালীগঞ্জ", "কোটচাঁদপুর", "মহেশপুর")
    ),
    BangladeshDistrict(
      "Magura", "মাগুরা", "Khulna",
      listOf("মাগুরা সদর", "শ্রীপুর", "শালিখা", "মহম্মদপুর")
    ),
    BangladeshDistrict(
      "Narail", "নড়াইল", "Khulna",
      listOf("নড়াইল সদর", "লোহাগড়া", "কালিয়া", "নড়াগাতি")
    ),
    BangladeshDistrict(
      "Chuadanga", "চুয়াডাঙ্গা", "Khulna",
      listOf("চুয়াডাঙ্গা সদর", "আলমডাঙ্গা", "দামুড়হুদা", "জীবননগর")
    ),
    BangladeshDistrict(
      "Meherpur", "মেহেরপুর", "Khulna",
      listOf("মেহেরপুর সদর", "গাংনী", "মুজিবনগর")
    ),

    // Barishal Division (6)
    BangladeshDistrict(
      "Barishal", "বরিশাল", "Barishal",
      listOf("বরিশাল সদর (কোতোয়ালি)", "বাকেরগঞ্জ", "বাবুগঞ্জ", "বানারীপাড়া", "উজিরপুর", "গৌরনদী", "আগৈলঝাড়া", "মুলাদী", "হিজলা", "মেহেন্দিগঞ্জ")
    ),
    BangladeshDistrict(
      "Patuakhali", "পটুয়াখালী", "Barishal",
      listOf("পটুয়াখালী সদর", "বাউফল", "দশমিনা", "গলাচিপা", "কলাপাড়া", "দুমকি", "মির্জাগঞ্জ", "রাঙ্গাবালী")
    ),
    BangladeshDistrict(
      "Bhola", "ভোলা", "Barishal",
      listOf("ভোলা সদর", "দৌলতখান", "বোরহানউদ্দিন", "তজুমদ্দিন", "লালমোহন", "চরফ্যাশন", "মনপুরা")
    ),
    BangladeshDistrict(
      "Pirojpur", "পিরোজপুর", "Barishal",
      listOf("পিরোজপুর সদর", "নাজিরপুর", "কাউখালী", "ভাণ্ডারিয়া", "মঠবাড়িয়া", "নেছারাবাদ (স্বরূপকাঠি)", "ইন্দুরকানী")
    ),
    BangladeshDistrict(
      "Barguna", "বরগুনা", "Barishal",
      listOf("বরগুনা সদর", "আমতলী", "তালতলী", "বেতাগী", "বামনা", "পাথরঘাটা")
    ),
    BangladeshDistrict(
      "Jhalokathi", "ঝালকাঠি", "Barishal",
      listOf("ঝালকাঠি সদর", "নলছিটি", "রাজাপুর", "কাঁঠালিয়া")
    ),

    // Sylhet Division (4)
    BangladeshDistrict(
      "Sylhet", "সিলেট", "Sylhet",
      listOf("সিলেট সদর (কোতোয়ালি)", "জালালাবাদ", "দক্ষিণ সুরমা", "শাহপরাণ", "বিশ্বনাথ", "বালাগঞ্জ", "ওসমানীনগর", "ফেঞ্চুগঞ্জ", "গোলাপগঞ্জ", "বিয়ানীবাজার", "জকিগঞ্জ", "কানাইঘাট", "জৈন্তাপুর", "গোয়াইনঘাট", "কোম্পানীগঞ্জ")
    ),
    BangladeshDistrict(
      "Moulvibazar", "মৌলভীবাজার", "Sylhet",
      listOf("মৌলভীবাজার সদর", "শ্রীমঙ্গল", "কমলগঞ্জ", "কুলাউড়া", "জুড়ী", "বড়লেখা", "রাজনগর")
    ),
    BangladeshDistrict(
      "Habiganj", "হবিগঞ্জ", "Sylhet",
      listOf("হবিগঞ্জ সদর", "শায়েস্তাগঞ্জ", "নবীগঞ্জ", "বাহুবল", "চুনারুঘাট", "মাধবপুর", "লাখাই", "বানিয়াচং", "আজমিরীগঞ্জ")
    ),
    BangladeshDistrict(
      "Sunamganj", "সুনামগঞ্জ", "Sylhet",
      listOf("সুনামগঞ্জ সদর", "ছাতক", "দোয়ারাবাজার", "বিশ্বম্ভরপুর", "তাহিরপুর", "জামালগঞ্জ", "ধর্মপাশা", "মধ্যনগর", "দিরাই", "শাল্লা", "জগন্নাথপুর", "শান্তিগঞ্জ")
    ),

    // Rangpur Division (8)
    BangladeshDistrict(
      "Rangpur", "রংপুর", "Rangpur",
      listOf("রংপুর সদর (কোতোয়ালি)", "তাজহাট", "হারাগাছ", "গঙ্গাচড়া", "তারাগঞ্জ", "বদরগঞ্জ", "মিঠাপুকুর", "পীরগাছা", "কাউনিয়া", "পীরগঞ্জ")
    ),
    BangladeshDistrict(
      "Dinajpur", "দিনাজপুর", "Rangpur",
      listOf("দিনাজপুর সদর", "বিরল", "বোচাগঞ্জ", "কাহারোল", "বীরগঞ্জ", "খানসামা", "চিরিরবন্দর", "পার্বতীপুর", "ফুলবাড়ী", "বিরামপুর", "নবাবগঞ্জ", "হাকিমপুর", "ঘোড়াঘাট")
    ),
    BangladeshDistrict(
      "Kurigram", "কুড়িগ্রাম", "Rangpur",
      listOf("কুড়িগ্রাম সদর", "নাগেশ্বরী", "ভূরুঙ্গামারী", "ফুলবাড়ী", "রাজারহাট", "উলিপুর", "চিলমারী", "রৌমারী", "চর রাজিবপুর")
    ),
    BangladeshDistrict(
      "Gaibandha", "গাইবান্ধা", "Rangpur",
      listOf("গাইবান্ধা সদর", "সাদুল্লাপুর", "সুন্দরগঞ্জ", "পলাশবাড়ী", "গোবিন্দগঞ্জ", "সাঘাটা", "ফুলছড়ি")
    ),
    BangladeshDistrict(
      "Nilphamari", "নীলফামারী", "Rangpur",
      listOf("নীলফামারী সদর", "সৈয়দপুর", "ডোমার", "ডিমলা", "জলঢাকা", "কিশোরগঞ্জ")
    ),
    BangladeshDistrict(
      "Lalmonirhat", "লালমনিরহাট", "Rangpur",
      listOf("লালমনিরহাট সদর", "আদিতমারী", "কালীগঞ্জ", "হাতীবান্ধা", "পাটগ্রাম")
    ),
    BangladeshDistrict(
      "Thakurgaon", "ঠাকুরগাঁও", "Rangpur",
      listOf("ঠাকুরগাঁও সদর", "পীরগঞ্জ", "রানীশংকৈল", "হরিপুর", "বালিয়াডাঙ্গী")
    ),
    BangladeshDistrict(
      "Panchagarh", "পঞ্চগড়", "Rangpur",
      listOf("পঞ্চগড় সদর", "বোদা", "দেবীগঞ্জ", "আটোয়ারী", "তেঁতুলিয়া")
    ),

    // Mymensingh Division (4)
    BangladeshDistrict(
      "Mymensingh", "ময়মনসিংহ", "Mymensingh",
      listOf("ময়মনসিংহ সদর", "মুক্তাগাছা", "ফুলবাড়িয়া", "ত্রিশাল", "ভালুকা", "গফরগাঁও", "ঈশ্বরগঞ্জ", "নান্দাইল", "গৌরীপুর", "তারাকান্দা", "ফুলপুর", "হালুয়াঘাট", "ধোবাউড়া")
    ),
    BangladeshDistrict(
      "Jamalpur", "জামালপুর", "Mymensingh",
      listOf("জামালপুর সদর", "মেলান্দহ", "ইসলামপুর", "দেওয়ানগঞ্জ", "বকশীগঞ্জ", "মাদারগঞ্জ", "সরিষাবাড়ী")
    ),
    BangladeshDistrict(
      "Sherpur", "শেরপুর", "Mymensingh",
      listOf("শেরপুর সদর", "নালিতাবাড়ী", "নকলা", "ঝিনাইগাতী", "শ্রীবরদী")
    ),
    BangladeshDistrict(
      "Netrokona", "নেত্রকোণা", "Mymensingh",
      listOf("নেত্রকোণা সদর", "পূর্বধলা", "দুর্গাপুর", "কলমাকান্দা", "বারহাট্টা", "আটপাড়া", "কেন্দুয়া", "মদন", "মোহনগঞ্জ", "খালিয়াজুরী")
    )
  )

  fun findDistrict(query: String): BangladeshDistrict? {
    val clean = query.trim().lowercase()
    if (clean.isEmpty()) return null
    return districts.firstOrNull {
      it.nameEn.lowercase() == clean ||
        it.nameBn == query.trim() ||
        it.displayLabel.lowercase() == clean ||
        clean.contains(it.nameEn.lowercase()) ||
        clean.contains(it.nameBn)
    }
  }

  fun searchDistricts(query: String): List<BangladeshDistrict> {
    val clean = query.trim().lowercase()
    if (clean.isEmpty()) return districts
    return districts.filter {
      it.nameEn.lowercase().contains(clean) ||
        it.nameBn.contains(clean) ||
        it.division.lowercase().contains(clean) ||
        it.thanas.any { th -> th.lowercase().contains(clean) }
    }
  }

  fun getThanasForDistrict(districtLabelOrName: String): List<String> {
    return findDistrict(districtLabelOrName)?.thanas ?: listOf("সদর")
  }

  fun isChandpurDistrict(districtLabelOrName: String): Boolean {
    val clean = districtLabelOrName.trim().lowercase()
    return clean.contains("chandpur") || districtLabelOrName.contains("চাঁদপুর")
  }

  fun calculateShippingFee(
    subtotal: Int,
    district: String,
    deliveryType: String,
    shippingInfo: com.example.data.model.ShippingInfoConfig
  ): Int {
    val allowExpress = isChandpurDistrict(district)
    val effectiveType = if (deliveryType.equals("Express", ignoreCase = true) && allowExpress) "Express" else "Standard"
    val matchedOpt = shippingInfo.options.firstOrNull {
      it.enabled && it.deliveryType.equals(effectiveType, ignoreCase = true)
    }

    if (matchedOpt != null) {
      val districtValid = when (matchedOpt.districtRule) {
        "CHANDPUR_ONLY" -> allowExpress
        "CUSTOM" -> matchedOpt.customDistricts.isEmpty() || matchedOpt.customDistricts.any { d ->
          district.contains(d, ignoreCase = true)
        }
        else -> true
      }
      if (districtValid) {
        if (matchedOpt.freeAbove > 0 && subtotal >= matchedOpt.freeAbove && effectiveType != "Express") {
          return 0
        }
        if (effectiveType == "Standard" && allowExpress) {
          return shippingInfo.chandpurStandardFee
        }
        return matchedOpt.fee
      }
    }

    if (effectiveType == "Express") {
      return 200
    }
    if (subtotal >= 2000) return 0
    return if (allowExpress) shippingInfo.chandpurStandardFee else 120
  }
}
