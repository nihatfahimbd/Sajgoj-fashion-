import re

def refactor():
    with open("index.html", "r", encoding="utf-8") as f:
        content = f.read()

    # 1. Update storefront button
    old_storefront_btn = """                    <button 
                      onClick={() => { setActiveTab("Admin"); setIsAdminDrawerOpen(true); }}
                      className="border-2 border-[#7a1c2b] bg-[#fffaf5] text-[#7a1c2b] px-3 sm:px-4 py-1.5 rounded-full text-xs font-bold hover:bg-[#7a1c2b] hover:text-white transition shadow-sm flex items-center gap-1.5"
                    >
                      <span>⚙️</span>
                      <span className="hidden sm:inline">এডমিন ড্যাশবোর্ড</span>
                      <span className="sm:hidden">এডমিন</span>
                    </button>"""

    new_storefront_btn = """                    <button 
                      onClick={() => setActiveTab("Dashboard")}
                      className="border-2 border-[#7a1c2b] bg-[#fffaf5] text-[#7a1c2b] px-3 sm:px-4 py-1.5 rounded-full text-xs font-bold hover:bg-[#7a1c2b] hover:text-white transition shadow-sm flex items-center gap-1.5 active:scale-95"
                      title="এডমিন কন্ট্রোল সেন্টার ও ড্যাশবোর্ড"
                    >
                      <span>⚙️</span>
                      <span className="hidden sm:inline">এডমিন ড্যাশবোর্ড</span>
                      <span className="sm:hidden">এডমিন</span>
                    </button>"""

    assert old_storefront_btn in content, "old_storefront_btn not found"
    content = content.replace(old_storefront_btn, new_storefront_btn, 1)

    # 2. Update adminTabs definition
    old_admin_tabs = """      const adminTabs = [
        { id: "Dashboard", label: "ড্যাশবোর্ড", icon: "📊" },
        { id: "Combos", label: "কম্বো প্যাক", icon: "🎁" },
        { id: "Products", label: "প্রোডাক্টস", icon: "👗" },
        { id: "Orders", label: "অর্ডারস", icon: "📦" },
        { id: "Categories", label: "ক্যাটাগরি", icon: "🏷️" },
        { id: "Coupons", label: "কুপন কোড", icon: "🎟️" },
        { id: "Banners", label: "ব্যানার সেটিংস", icon: "🖼️" },
        { id: "Courier", label: "কুরিয়ার সেটিংস", icon: "🚚" },
        { id: "Pixel", label: "ফেসবুক পিক্সেল", icon: "📈" },
        { id: "SEO", label: "এসইও ও সাইটম্যাপ", icon: "🌐" },
        { id: "Settings", label: "স্টোর ও লোগো সেটিংস", icon: "⚙️" },
        { id: "Backup", label: "ডাটাবেজ ব্যাকআপ", icon: "💾" }
      ];"""

    new_admin_tabs = """      const adminTabs = [
        { id: "Dashboard", label: "ড্যাশবোর্ড", icon: "📊" },
        { id: "Orders", label: "অর্ডার ট্র্যাকিং", icon: "📦", count: orders.length },
        { id: "Products", label: "প্রোডাক্টস ও কম্বো", icon: "👗", count: products.length },
        { id: "General", label: "থিম কাস্টমাইজেশন", icon: "🎨" },
        { id: "Loyalty", label: "সিস্টেম কনফিগারেশন", icon: "⚙️" }
      ];"""

    assert old_admin_tabs in content, "old_admin_tabs not found"
    content = content.replace(old_admin_tabs, new_admin_tabs, 1)

    # 3. Update top master navigation bar & sub-navigation pills
    old_nav_marker = "                {/* CONSOLIDATED MASTER ADMIN DASHBOARD NAVIGATION BAR */}"
    end_nav_marker = "              </header>\n\n              {/* Main Admin Content Body */}"
    
    idx_nav_start = content.find(old_nav_marker)
    assert idx_nav_start != -1, "nav start not found"
    idx_nav_end = content.find(end_nav_marker)
    assert idx_nav_end != -1, "nav end not found"

    new_nav_block = """                {/* CONSOLIDATED MASTER ADMIN DASHBOARD NAVIGATION BAR */}
                <div className="border-t border-[#f3e2d0] bg-[#fffaf5] px-4 shadow-inner">
                  {/* Master 5 Administrative Pillars */}
                  <div className="max-w-7xl mx-auto flex items-center justify-between gap-2 overflow-x-auto py-2.5 scrollbar-hide text-xs sm:text-sm font-bold">
                    <div className="flex items-center gap-2 flex-nowrap">
                      {/* 1. Dashboard Overview */}
                      <button
                        onClick={() => setActiveTab("Dashboard")}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition flex items-center gap-2 ${
                          activeTab === "Dashboard"
                            ? "bg-[#7c1d2a] text-white shadow-md font-extrabold scale-[1.02]"
                            : "bg-white text-gray-700 hover:bg-[#f2e7db] border border-[#ecd9c6]"
                        }`}
                      >
                        <span>📊</span>
                        <span>ড্যাশবোর্ড</span>
                      </button>

                      {/* 2. Order Tracking */}
                      <button
                        onClick={() => {
                          setActiveTab("Orders");
                          setNewOrdersCount(0);
                          setNewOrderAlert(null);
                        }}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition flex items-center gap-2 ${
                          activeTab === "Orders"
                            ? "bg-[#7c1d2a] text-white shadow-md font-extrabold scale-[1.02]"
                            : "bg-white text-gray-700 hover:bg-[#f2e7db] border border-[#ecd9c6]"
                        }`}
                      >
                        <span>📦</span>
                        <span>অর্ডার ট্র্যাকিং</span>
                        <span className={`text-[11px] px-2 py-0.5 rounded-full font-bold ${activeTab === "Orders" ? 'bg-white/25 text-white' : 'bg-[#fff0e6] text-[#7a1c2b] border border-[#f3e2d0]'}`}>
                          {orders.length}
                        </span>
                        {newOrdersCount > 0 && (
                          <span className="bg-rose-500 text-white text-[10px] font-black px-1.5 py-0.5 rounded-full animate-bounce shadow-xs">
                            +{newOrdersCount}
                          </span>
                        )}
                      </button>

                      {/* 3. Product Management */}
                      <button
                        onClick={() => {
                          if (!["Products", "Combos", "Categories", "Coupons", "Reviews"].includes(activeTab)) {
                            setActiveTab("Products");
                          }
                        }}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition flex items-center gap-2 ${
                          ["Products", "Combos", "Categories", "Coupons", "Reviews"].includes(activeTab)
                            ? "bg-[#7c1d2a] text-white shadow-md font-extrabold scale-[1.02]"
                            : "bg-white text-gray-700 hover:bg-[#f2e7db] border border-[#ecd9c6]"
                        }`}
                      >
                        <span>👗</span>
                        <span>প্রোডাক্টস ও কম্বো</span>
                        <span className={`text-[11px] px-2 py-0.5 rounded-full font-bold ${["Products", "Combos", "Categories", "Coupons", "Reviews"].includes(activeTab) ? 'bg-white/25 text-white' : 'bg-[#fff0e6] text-[#7a1c2b] border border-[#f3e2d0]'}`}>
                          {products.length}
                        </span>
                      </button>

                      {/* 4. Theme Customization */}
                      <button
                        onClick={() => {
                          if (!["General", "Settings", "Admin", "Banners", "Header", "Footer"].includes(activeTab)) {
                            setActiveTab("General");
                          }
                        }}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition flex items-center gap-2 ${
                          ["General", "Settings", "Admin", "Banners", "Header", "Footer"].includes(activeTab)
                            ? "bg-[#7c1d2a] text-white shadow-md font-extrabold scale-[1.02]"
                            : "bg-white text-gray-700 hover:bg-[#f2e7db] border border-[#ecd9c6]"
                        }`}
                      >
                        <span>🎨</span>
                        <span>থিম কাস্টমাইজেশন</span>
                        <span className="w-2.5 h-2.5 rounded-full shadow-xs border border-white" style={{ backgroundColor: config.primaryColor || "#7a1c2b" }}></span>
                      </button>

                      {/* 5. System Configurations */}
                      <button
                        onClick={() => {
                          if (!["Loyalty", "Courier", "Pixel", "SEO", "Backup"].includes(activeTab)) {
                            setActiveTab("Loyalty");
                          }
                        }}
                        className={`px-4 py-2 rounded-full whitespace-nowrap transition flex items-center gap-2 ${
                          ["Loyalty", "Courier", "Pixel", "SEO", "Backup"].includes(activeTab)
                            ? "bg-[#7c1d2a] text-white shadow-md font-extrabold scale-[1.02]"
                            : "bg-white text-gray-700 hover:bg-[#f2e7db] border border-[#ecd9c6]"
                        }`}
                      >
                        <span>⚙️</span>
                        <span>সিস্টেম কনফিগারেশন</span>
                        <span className="text-[10px] bg-emerald-100 text-emerald-800 px-1.5 py-0.5 rounded font-mono font-bold">Cloud</span>
                      </button>
                    </div>
                  </div>

                  {/* COHESIVE SUB-NAVIGATION PILLS (FOR PRODUCT MANAGEMENT) */}
                  {["Products", "Combos", "Categories", "Coupons", "Reviews"].includes(activeTab) && (
                    <div className="max-w-7xl mx-auto flex items-center gap-2 overflow-x-auto py-2 border-t border-[#f3e2d0] scrollbar-hide text-xs font-bold animate-fadeIn">
                      <span className="text-[#b8956a] whitespace-nowrap text-[11px] font-bold mr-1">প্রোডাক্ট মডিউল:</span>
                      {[
                        { id: "Products", label: "🛍️ সকল প্রোডাক্ট ও BOGO", count: products.length },
                        { id: "Combos", label: "🎁 কম্বো প্যাক বিল্ডার", count: combos.length },
                        { id: "Categories", label: "🏷️ ক্যাটাগরি", count: categories.length },
                        { id: "Coupons", label: "🎟️ কুপন ও ছাড়", count: coupons.length },
                        { id: "Reviews", label: "⭐ কাস্টমার রিভিউ", count: reviews.length }
                      ].map(sub => (
                        <button
                          key={sub.id}
                          onClick={() => setActiveTab(sub.id)}
                          className={`px-3.5 py-1.5 rounded-full whitespace-nowrap transition flex items-center gap-1.5 ${
                            activeTab === sub.id
                              ? "bg-[#7a1c2b] text-white shadow-xs font-extrabold scale-[1.02]"
                              : "bg-white text-gray-700 hover:bg-[#fff8f0] border border-[#ecd9c6]"
                          }`}
                        >
                          <span>{sub.label}</span>
                          {sub.count !== undefined && (
                            <span className={`text-[10px] px-1.5 py-0.2 rounded-full font-bold ${activeTab === sub.id ? 'bg-white/25 text-white' : 'bg-gray-100 text-gray-700'}`}>
                              {sub.count}
                            </span>
                          )}
                        </button>
                      ))}
                    </div>
                  )}

                  {/* COHESIVE SUB-NAVIGATION PILLS (FOR THEME CUSTOMIZATION) */}
                  {["General", "Settings", "Admin", "Banners", "Header", "Footer"].includes(activeTab) && (
                    <div className="max-w-7xl mx-auto flex items-center gap-2 overflow-x-auto py-2 border-t border-[#f3e2d0] scrollbar-hide text-xs font-bold animate-fadeIn">
                      <span className="text-[#b8956a] whitespace-nowrap text-[11px] font-bold mr-1">থিম ও স্টোরফ্রন্ট:</span>
                      {[
                        { id: "General", label: "🎨 ব্র্যান্ড, লোগো ও কালার" },
                        { id: "Banners", label: `🖼️ ব্যানার স্লাইডার (${banners.length})` },
                        { id: "Header", label: `📢 নোটিশ বার ও হেডার (${config.announcement?.enabled ? "চালু" : "বন্ধ"})` },
                        { id: "Footer", label: "📄 ফুটার ও সোশ্যাল লিঙ্ক" }
                      ].map(sub => (
                        <button
                          key={sub.id}
                          onClick={() => setActiveTab(sub.id)}
                          className={`px-3.5 py-1.5 rounded-full whitespace-nowrap transition flex items-center gap-1.5 ${
                            activeTab === sub.id || (sub.id === "General" && (activeTab === "Settings" || activeTab === "Admin"))
                              ? "bg-[#7a1c2b] text-white shadow-xs font-extrabold scale-[1.02]"
                              : "bg-white text-gray-700 hover:bg-[#fff8f0] border border-[#ecd9c6]"
                          }`}
                        >
                          <span>{sub.label}</span>
                        </button>
                      ))}
                    </div>
                  )}

                  {/* COHESIVE SUB-NAVIGATION PILLS (FOR SYSTEM CONFIGURATIONS) */}
                  {["Loyalty", "Courier", "Pixel", "SEO", "Backup"].includes(activeTab) && (
                    <div className="max-w-7xl mx-auto flex items-center gap-2 overflow-x-auto py-2 border-t border-[#f3e2d0] scrollbar-hide text-xs font-bold animate-fadeIn">
                      <span className="text-[#b8956a] whitespace-nowrap text-[11px] font-bold mr-1">সিস্টেম কনফিগারেশন:</span>
                      {[
                        { id: "Loyalty", label: "👑 VIP মেম্বারশিপ ও রিওয়ার্ড" },
                        { id: "Courier", label: "🚚 কুরিয়ার ইন্টিগ্রেশন (Steadfast / Pathao)" },
                        { id: "Pixel", label: "📈 ফেসবুক পিক্সেল ও CAPI" },
                        { id: "SEO", label: "🌐 এসইও ও সোশ্যাল শেয়ার" },
                        { id: "Backup", label: "💾 ক্লাউড ব্যাকআপ ও ডাটাবেজ" }
                      ].map(sub => (
                        <button
                          key={sub.id}
                          onClick={() => setActiveTab(sub.id)}
                          className={`px-3.5 py-1.5 rounded-full whitespace-nowrap transition flex items-center gap-1.5 ${
                            activeTab === sub.id
                              ? "bg-[#7a1c2b] text-white shadow-xs font-extrabold scale-[1.02]"
                              : "bg-white text-gray-700 hover:bg-[#fff8f0] border border-[#ecd9c6]"
                          }`}
                        >
                          <span>{sub.label}</span>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
"""
    content = content[:idx_nav_start] + new_nav_block + content[idx_nav_end:]

    # 4. Streamline redundant 9-module card grid in Dashboard
    old_dashboard_redundant = """                    {/* CONSOLIDATED SECTION 4: STREAMLINED CONFIGURATION TOOLS HUB */}
                    <div className="space-y-4">
                      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2">
                        <div>
                          <h3 className="font-bold text-lg text-[#7a1c2b] flex items-center gap-2">
                            <span>🛠️</span>
                            <span>স্টোর কনফিগারেশন ও ম্যানেজমেন্ট হাব</span>
                          </h3>
                          <p className="text-xs text-gray-500">ব্যানার, কুপন, ক্যাটাগরি, রিভিউ, পিক্সেল, কুরিয়ার ও ব্যাকআপ সেটিংস</p>
                        </div>
                        <button
                          onClick={() => setActiveTab("General")}
                          className="text-xs font-bold text-[#7a1c2b] bg-white px-3.5 py-1.5 rounded-full border border-[#f3e2d0] hover:bg-[#7a1c2b] hover:text-white transition shadow-xs flex items-center gap-1.5"
                        >
                          <span>⚙️</span>
                          <span>পূর্ণাঙ্গ স্টোর ইনফো ও কনফিগ →</span>
                        </button>
                      </div>

                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
                        {/* Module 0: Combos */}
                        <div 
                          onClick={() => setActiveTab("Combos")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#d4a574] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🎁
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">কম্বো প্যাক অফার</h4>
                            <p className="text-xs text-gray-500 mt-1">প্রোডাক্ট বান্ডেল ও বিশেষ মূল্য নির্ধারণ</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            {combos.length} টি কম্বো প্যাক →
                          </span>
                        </div>

                        {/* Module 1: Categories */}
                        <div 
                          onClick={() => setActiveTab("Categories")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🏷️
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">ক্যাটাগরি সেটিংস</h4>
                            <p className="text-xs text-gray-500 mt-1">নতুন ক্যাটাগরি তৈরি ও সাজানো</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            {categories.length} টি ক্যাটাগরি →
                          </span>
                        </div>

                        {/* Module 2: Coupons */}
                        <div 
                          onClick={() => setActiveTab("Coupons")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🎟️
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">কুপন ও ছাড়</h4>
                            <p className="text-xs text-gray-500 mt-1">ডিসকাউন্ট কোড ও অফার তৈরি</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            {coupons.length} টি কুপন →
                          </span>
                        </div>

                        {/* Module 3: Banners */}
                        <div 
                          onClick={() => setActiveTab("Banners")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🖼️
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">ব্যানার ও স্লাইডার</h4>
                            <p className="text-xs text-gray-500 mt-1">হোমপেজ ব্যানার, ছবি ও অফার টেক্সট</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            {banners.length} টি ব্যানার →
                          </span>
                        </div>

                        {/* Module 4: Reviews */}
                        <div 
                          onClick={() => setActiveTab("Reviews")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              ⭐
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">কাস্টমার রিভিউ</h4>
                            <p className="text-xs text-gray-500 mt-1">গ্রাহকদের ফিডব্যাক ও রেটিং</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            {reviews.length} টি রিভিউ →
                          </span>
                        </div>

                        {/* Module 5: Courier API */}
                        <div 
                          onClick={() => setActiveTab("Courier")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🚚
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">কুরিয়ার ও ডেলিভারি</h4>
                            <p className="text-xs text-gray-500 mt-1">Steadfast ও Pathao API ও চার্জ</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            কুরিয়ার সেটআপ →
                          </span>
                        </div>

                        {/* Module 6: Pixel */}
                        <div 
                          onClick={() => setActiveTab("Pixel")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              📈
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">ফেসবুক পিক্সেল</h4>
                            <p className="text-xs text-gray-500 mt-1">Meta Pixel ID ও কনভার্সন ট্র্যাকিং</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            পিক্সেল কোড সেট →
                          </span>
                        </div>

                        {/* Module 7: SEO */}
                        <div 
                          onClick={() => setActiveTab("SEO")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              🔍
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">এসইও ও সাইটম্যাপ</h4>
                            <p className="text-xs text-gray-500 mt-1">Google সার্চ ইনডেক্স ও মেটা ট্যাগ</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            এসইও অপশন →
                          </span>
                        </div>

                        {/* Module 8: Backup */}
                        <div 
                          onClick={() => setActiveTab("Backup")} 
                          className="bg-white p-5 rounded-3xl border-2 border-[#f3e2d0] hover:border-[#7a1c2b] cursor-pointer shadow-sm hover:shadow-md transition text-left flex flex-col justify-between"
                        >
                          <div>
                            <div className="w-11 h-11 rounded-2xl bg-[#fff8f0] border border-[#d4a574] flex items-center justify-center text-xl mb-3">
                              💾
                            </div>
                            <h4 className="font-bold text-sm text-[#2c1810]">ডাটাবেজ ব্যাকআপ</h4>
                            <p className="text-xs text-gray-500 mt-1">JSON ফাইল ব্যাকআপ ও সরাসরি রিস্টোর</p>
                          </div>
                          <span className="text-xs text-[#7a1c2b] font-bold mt-3 block">
                            ব্যাকআপ সেন্টার →
                          </span>
                        </div>
                      </div>
                    </div>"""

    new_dashboard_streamlined = """                    {/* STREAMLINED QUICK NAVIGATION HUB */}
                    <div className="p-5 sm:p-6 bg-gradient-to-br from-white to-[#fffaf5] rounded-3xl border border-[#ecd9c6] shadow-sm flex flex-col sm:flex-row items-center justify-between gap-4">
                      <div className="flex items-center gap-3">
                        <div className="w-12 h-12 rounded-2xl bg-[#7a1c2b]/10 text-[#7a1c2b] flex items-center justify-center text-2xl border border-[#7a1c2b]/20">
                          ⚡
                        </div>
                        <div>
                          <h4 className="font-bold text-sm text-[#2c1810]">দ্রুত এক্সেস ও কনফিগারেশন হাব</h4>
                          <p className="text-xs text-gray-500">প্রোডাক্ট, থিম, অর্ডার এবং ব্যাকআপ মডিউলে সরাসরি নেভিগেট করুন</p>
                        </div>
                      </div>
                      <div className="flex flex-wrap items-center gap-2">
                        <button
                          onClick={() => setActiveTab("Products")}
                          className="px-3.5 py-1.5 rounded-full text-xs font-bold bg-[#fff8f0] border border-[#d4a574] text-[#7a1c2b] hover:bg-[#7a1c2b] hover:text-white transition shadow-xs"
                        >
                          👗 প্রোডাক্ট হাব ({products.length})
                        </button>
                        <button
                          onClick={() => setActiveTab("General")}
                          className="px-3.5 py-1.5 rounded-full text-xs font-bold bg-[#fff8f0] border border-[#ecd9c6] text-gray-700 hover:bg-[#7a1c2b] hover:text-white transition shadow-xs"
                        >
                          🎨 থিম ও লোগো
                        </button>
                        <button
                          onClick={() => setActiveTab("Loyalty")}
                          className="px-3.5 py-1.5 rounded-full text-xs font-bold bg-[#fff8f0] border border-amber-300 text-amber-900 hover:bg-amber-600 hover:text-white transition shadow-xs"
                        >
                          👑 VIP ক্লাব
                        </button>
                        <button
                          onClick={() => setActiveTab("Backup")}
                          className="px-3.5 py-1.5 rounded-full text-xs font-bold bg-white border border-emerald-300 text-emerald-800 hover:bg-emerald-600 hover:text-white transition shadow-xs"
                        >
                          💾 ক্লাউড ব্যাকআপ
                        </button>
                      </div>
                    </div>"""

    assert old_dashboard_redundant in content, "old_dashboard_redundant not found"
    content = content.replace(old_dashboard_redundant, new_dashboard_streamlined, 1)

    # 5. Modularize General Settings into clean General, Header, Footer tabs and remove redundant 11-pill sub bar
    p_general = re.search(r"\{\/\* =+\s*\*\/\}\s*\{\/\* 11\. GENERAL SETTINGS \*\/\}[\s\S]*?(?=\{\/\* =+\s*\*\/\}\s*\{\/\* 12\. BACKUP & RESTORE TAB \*\/\} )", content)
    assert p_general, "General settings block not found"

    new_general_replacement = """{/* ========================================================= */}
            {/* 11.1. THEME CUSTOMIZATION: BRAND, LOGO, COLOR PALETTE & SECURITY */}
            {/* ========================================================= */}
            {(activeTab === "General" || activeTab === "Settings" || activeTab === "Admin") && (
              <div className="bg-white p-6 sm:p-8 rounded-3xl border border-[#f3e2d0] space-y-6 shadow-sm animate-fadeIn">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-[#f3e2d0]">
                  <div>
                    <h3 className="text-xl sm:text-2xl font-bold text-[#7a1c2b] flex items-center gap-2">
                      <span>🎨</span>
                      <span>ব্র্যান্ড, লোগো ও রাজকীয় কালার থিম</span>
                    </h3>
                    <p className="text-xs text-gray-500 mt-0.5">
                      স্টোরের নাম, স্লোগান, ব্র্যান্ড লোগো, ফ্যাভিকন, লাক্সারি কালার প্যালেট ও এডমিন সেশন নিয়ন্ত্রণ করুন
                    </p>
                  </div>
                  <button 
                    onClick={async () => {
                      setStored("config", config);
                      await syncDocToCloud("config", "store", config);
                      notify("ব্র্যান্ড ও থিম সেটিংস সফলভাবে ক্লাউডে সংরক্ষিত হয়েছে! 🎨👑");
                    }} 
                    className="bg-[#7a1c2b] hover:bg-[#63141f] text-white px-6 py-2.5 rounded-full text-xs font-bold transition shadow flex items-center gap-1.5 active:scale-95"
                  >
                    <span>💾</span>
                    <span>সেটিংস সংরক্ষণ করুন</span>
                  </button>
                </div>

                <div className="space-y-6">
                  {/* Brand, Logo & Favicon Upload Section */}
                  <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-4">
                    <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">১. ব্র্যান্ড আইডেন্টিটি, লোগো ও ফ্যাভিকন</h4>
                    
                    <div className="grid md:grid-cols-2 gap-4">
                      <div>
                        <label className="text-xs font-semibold text-gray-700">স্টোরের নাম (Store Name)</label>
                        <input 
                          type="text" 
                          value={config.storeName} 
                          onChange={(e) => setConfig({ ...config, storeName: e.target.value })}
                          className="w-full bg-white border border-[#c9b49e] rounded-full px-5 py-2.5 text-sm font-bold text-[#7a1c2b] outline-none mt-1 focus:border-[#7a1c2b]" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">ট্যাগলাইন / স্লোগান (Tagline)</label>
                        <input 
                          type="text" 
                          value={config.tagline} 
                          onChange={(e) => setConfig({ ...config, tagline: e.target.value })}
                          className="w-full bg-white border border-[#c9b49e] rounded-full px-5 py-2.5 text-sm outline-none mt-1 focus:border-[#7a1c2b]" 
                        />
                      </div>
                    </div>

                    {/* Logo Upload / URL */}
                    <div className="grid md:grid-cols-2 gap-4 pt-2">
                      <div className="space-y-2">
                        <div className="flex items-center justify-between">
                          <label className="text-xs font-semibold text-gray-700">স্টোর লোগো (Logo Image)</label>
                          <span className="text-[10px] text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">ক্লাউড অটো-সেভ ✓</span>
                        </div>
                        <div className="flex items-center gap-3">
                          {config.logo ? (
                            <img src={config.logo} alt="Logo Preview" className="w-14 h-14 rounded-2xl object-cover border-2 border-[#7a1c2b] bg-white p-1 shadow-sm" />
                          ) : (
                            <div className="w-14 h-14 rounded-2xl bg-[#7a1c2b] text-white flex items-center justify-center text-2xl shadow-sm">👑</div>
                          )}
                          <div className="flex-1 space-y-1.5">
                            <input 
                              type="file" 
                              accept="image/*" 
                              ref={logoFileRef} 
                              onChange={(e) => handleImageUpload(e, (dataUrl) => handleLogoUpdate(dataUrl), 'logo')} 
                              className="hidden" 
                            />
                            <button 
                              type="button" 
                              onClick={() => logoFileRef.current?.click()} 
                              className="bg-[#7a1c2b] text-white px-4 py-1.5 rounded-full text-xs font-semibold hover:bg-[#681727] shadow"
                            >
                              📷 লোগো আপলোড করুন
                            </button>
                            <input 
                              type="text" 
                              placeholder="অথবা লোগো URL দিন..." 
                              value={config.logo} 
                              onChange={(e) => {
                                const val = e.target.value;
                                setConfig(prev => ({ ...prev, logo: val }));
                              }}
                              onBlur={(e) => {
                                if (e.target.value) handleLogoUpdate(e.target.value);
                              }}
                              className="w-full bg-white border rounded-full px-3 py-1 text-xs outline-none focus:border-[#7a1c2b]" 
                            />
                          </div>
                        </div>
                      </div>

                      {/* Favicon Upload / URL */}
                      <div className="space-y-2">
                        <div className="flex items-center justify-between">
                          <label className="text-xs font-semibold text-gray-700">ওয়েবসাইট ফ্যাভিকন (Favicon Icon)</label>
                          <span className="text-[10px] text-emerald-700 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">ক্লাউড অটো-সেভ ✓</span>
                        </div>
                        <div className="flex items-center gap-3">
                          {config.favicon ? (
                            <img src={config.favicon} alt="Favicon Preview" className="w-10 h-10 rounded-xl object-contain border p-1 bg-white shadow-xs" />
                          ) : (
                            <div className="w-10 h-10 rounded-xl bg-gray-200 text-gray-500 flex items-center justify-center text-sm font-bold">ICO</div>
                          )}
                          <div className="flex-1 space-y-1.5">
                            <input 
                              type="file" 
                              accept="image/*" 
                              ref={faviconFileRef} 
                              onChange={(e) => handleImageUpload(e, (dataUrl) => handleFaviconUpdate(dataUrl), 'favicon')} 
                              className="hidden" 
                            />
                            <button 
                              type="button" 
                              onClick={() => faviconFileRef.current?.click()} 
                              className="bg-[#2c1810] text-white px-4 py-1.5 rounded-full text-xs font-semibold hover:bg-black shadow"
                            >
                              🌐 ফ্যাভিকন আপলোড
                            </button>
                            <input 
                              type="text" 
                              placeholder="অথবা ফ্যাভিকন URL দিন..." 
                              value={config.favicon} 
                              onChange={(e) => {
                                const val = e.target.value;
                                setConfig(prev => ({ ...prev, favicon: val }));
                              }}
                              onBlur={(e) => {
                                if (e.target.value) handleFaviconUpdate(e.target.value);
                              }}
                              className="w-full bg-white border rounded-full px-3 py-1 text-xs outline-none focus:border-[#7a1c2b]" 
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 2. Theme & Brand Color Customization Section */}
                  <div className="p-5 sm:p-6 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-4 shadow-sm">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                      <div>
                        <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider flex items-center gap-2">
                          <span>🎨</span>
                          <span>২. রাজকীয় ব্র্যান্ড কালার প্যালেট (Royal Luxury Color Palette)</span>
                        </h4>
                        <p className="text-xs text-gray-500 mt-0.5">
                          আপনার ব্র্যান্ডের প্রাইমারি কালার পরিবর্তন করুন, যা রিয়েলটাইমে সম্পূর্ণ স্টোরফ্রন্টে প্রযোজ্য হবে
                        </p>
                      </div>
                      <span className="text-[11px] font-bold px-3 py-1 rounded-full border shadow-xs flex items-center gap-1.5 self-start sm:self-auto bg-white" style={{ borderColor: config.primaryColor || '#7a1c2b', color: config.primaryColor || '#7a1c2b' }}>
                        <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: config.primaryColor || '#7a1c2b' }}></span>
                        {config.primaryColor || "#7a1c2b"}
                      </span>
                    </div>

                    <div className="grid md:grid-cols-2 gap-5 pt-1">
                      {/* Color Picker & Hex Input */}
                      <div className="bg-white p-4 rounded-2xl border border-[#f3e2d0] space-y-3">
                        <label className="text-xs font-semibold text-gray-700 block">কাস্টম কালার পিকার ও হেক্স কোড</label>
                        <div className="flex items-center gap-3">
                          <div className="relative w-12 h-12 rounded-2xl overflow-hidden border-2 shadow-sm flex-shrink-0 cursor-pointer" style={{ borderColor: config.primaryColor || '#7a1c2b' }}>
                            <input 
                              type="color" 
                              value={config.primaryColor || "#7a1c2b"} 
                              onChange={(e) => {
                                const newCol = e.target.value;
                                setConfig(prev => ({ ...prev, primaryColor: newCol }));
                                document.documentElement.style.setProperty('--primary-color', newCol);
                                document.documentElement.style.setProperty('--maroon', newCol);
                              }}
                              onBlur={(e) => handleThemeColorUpdate(e.target.value)}
                              className="absolute -inset-4 w-20 h-20 cursor-pointer opacity-100"
                              title="কালার নির্বাচন করুন"
                            />
                          </div>

                          <div className="flex-1 space-y-1">
                            <input 
                              type="text" 
                              placeholder="#7a1c2b" 
                              value={config.primaryColor || "#7a1c2b"} 
                              onChange={(e) => {
                                const newCol = e.target.value;
                                setConfig(prev => ({ ...prev, primaryColor: newCol }));
                                if (/^#[0-9A-Fa-f]{6}$/.test(newCol)) {
                                  document.documentElement.style.setProperty('--primary-color', newCol);
                                  document.documentElement.style.setProperty('--maroon', newCol);
                                }
                              }}
                              onBlur={(e) => {
                                if (/^#[0-9A-Fa-f]{6}$/.test(e.target.value)) {
                                  handleThemeColorUpdate(e.target.value);
                                }
                              }}
                              className="w-full bg-[#fffaf5] border rounded-full px-4 py-2 text-xs font-mono font-bold outline-none uppercase focus:border-[#7a1c2b]" 
                            />
                            <p className="text-[10px] text-gray-400">কালার পিকার আইকনে ক্লিক করে সরাসরি পছন্দ করুন</p>
                          </div>
                        </div>

                        <div className="pt-2 flex items-center justify-between border-t border-gray-100">
                          <button
                            type="button"
                            onClick={() => handleThemeColorUpdate("#7a1c2b")}
                            className="text-[11px] text-[#7a1c2b] font-bold hover:underline flex items-center gap-1"
                          >
                            <span>↺</span>
                            <span>ডিফল্ট মেরুন কালারে রিসেট করুন (#7a1c2b)</span>
                          </button>
                        </div>
                      </div>

                      {/* Live Storefront Component Preview */}
                      <div className="bg-white p-4 rounded-2xl border border-[#f3e2d0] space-y-3">
                        <label className="text-xs font-semibold text-gray-700 block">লাইভ কম্পোনেন্ট প্রিভিউ</label>
                        <div className="p-3.5 rounded-xl border border-dashed border-gray-200 bg-[#fffdfa] space-y-3">
                          <div className="flex items-center justify-between">
                            <span className="text-xs font-bold" style={{ color: config.primaryColor || '#7a1c2b' }}>
                              প্রোডাক্টের নাম (৩৫০০ ৳)
                            </span>
                            <span className="text-[10px] px-2 py-0.5 rounded-full font-bold text-white shadow-xs" style={{ backgroundColor: config.primaryColor || '#7a1c2b' }}>
                              বেস্টসেলার
                            </span>
                          </div>

                          <div className="flex gap-2">
                            <button 
                              type="button" 
                              className="flex-1 py-2 rounded-full text-xs font-bold text-white shadow transition"
                              style={{ backgroundColor: config.primaryColor || '#7a1c2b' }}
                            >
                              অর্ডার করুন →
                            </button>
                            <button 
                              type="button" 
                              className="px-4 py-2 rounded-full text-xs font-bold border transition bg-white"
                              style={{ borderColor: config.primaryColor || '#7a1c2b', color: config.primaryColor || '#7a1c2b' }}
                            >
                              কার্ট 🛒
                            </button>
                          </div>
                        </div>
                        <p className="text-[10px] text-emerald-700 font-semibold flex items-center gap-1">
                          <span>✓</span>
                          <span>CSS ভেরিয়েবল <code>--primary-color</code> ও <code>--maroon</code> এর মাধ্যমে পুরো স্টোরফ্রন্টে সংযুক্ত</span>
                        </p>
                      </div>
                    </div>

                    {/* Pre-designed Luxury Brand Color Presets */}
                    <div className="pt-2">
                      <p className="text-xs font-bold text-gray-700 mb-2">রেডিমেড লাক্সারি ব্র্যান্ড কালার প্যালেট (এক ক্লিকে পরিবর্তন):</p>
                      <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-2">
                        {[
                          { name: "Royal Maroon", color: "#7a1c2b", label: "রয়েল মেরুন (ডিফল্ট)" },
                          { name: "Emerald Green", color: "#064e3b", label: "এমেরাল্ড গ্রিন" },
                          { name: "Imperial Sapphire", color: "#1e3a8a", label: "স্যাফায়ার ব্লু" },
                          { name: "Regal Velvet", color: "#4c1d95", label: "ভেলভেট পার্পল" },
                          { name: "Crimson Rose", color: "#9f1239", label: "ক্রিমসন রোজ" },
                          { name: "Deep Chocolate", color: "#3b2219", label: "চকলেট ব্রাউন" },
                          { name: "Noir Obsidian", color: "#18181b", label: "অবসিডিয়ান ব্ল্যাক" }
                        ].map((preset) => {
                          const isSelected = (config.primaryColor || "#7a1c2b").toLowerCase() === preset.color.toLowerCase();
                          return (
                            <button
                              key={preset.color}
                              type="button"
                              onClick={() => handleThemeColorUpdate(preset.color)}
                              className={`p-2.5 rounded-2xl border text-center transition flex flex-col items-center gap-1.5 ${
                                isSelected ? "border-gray-900 shadow-md bg-white scale-[1.03] ring-2 ring-black/20" : "border-gray-200 bg-white hover:border-gray-400"
                              }`}
                            >
                              <span className="w-7 h-7 rounded-full shadow-inner flex items-center justify-center text-white text-[10px] font-bold" style={{ backgroundColor: preset.color }}>
                                {isSelected ? "✓" : ""}
                              </span>
                              <span className="text-[10px] font-bold text-gray-800 truncate w-full">{preset.label}</span>
                              <span className="text-[9px] font-mono text-gray-400">{preset.color}</span>
                            </button>
                          );
                        })}
                      </div>
                    </div>
                  </div>

                  {/* 3. Delivery Charges & Admin Security Section */}
                  <div className="grid md:grid-cols-2 gap-4">
                    {/* Delivery Charges */}
                    <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                      <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">৩. ডেলিভারি চার্জ সেটিংস</h4>
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="text-xs font-semibold text-gray-700">ঢাকার ভিতরে (৳)</label>
                          <input 
                            type="number" 
                            value={config.deliveryInsideDhaka} 
                            onChange={(e) => setConfig({ ...config, deliveryInsideDhaka: Number(e.target.value) })}
                            className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1" 
                          />
                        </div>
                        <div>
                          <label className="text-xs font-semibold text-gray-700">ঢাকার বাহিরে (৳)</label>
                          <input 
                            type="number" 
                            value={config.deliveryOutsideDhaka} 
                            onChange={(e) => setConfig({ ...config, deliveryOutsideDhaka: Number(e.target.value) })}
                            className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1" 
                          />
                        </div>
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">ফ্রি ডেলিভারি নূন্যতম অর্ডার মূল্য (৳)</label>
                        <input 
                          type="number" 
                          value={config.freeDeliveryThreshold} 
                          onChange={(e) => setConfig({ ...config, freeDeliveryThreshold: Number(e.target.value) })}
                          className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1" 
                        />
                      </div>
                    </div>

                    {/* Admin Access & Session Security */}
                    <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                      <div className="flex items-center justify-between">
                        <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">৪. এডমিন এক্সেস ও সিকিউরিটি</h4>
                        <span className="bg-emerald-100 text-emerald-800 text-[10px] px-2.5 py-0.5 rounded-full font-bold">
                          ● Active Session
                        </span>
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">মুদ্রা (Currency)</label>
                        <div className="w-full bg-white border rounded-full px-4 py-2 text-xs font-bold text-[#2c1810] mt-1">
                          BDT ৳ — Bangladesh Taka
                        </div>
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">এডমিন প্যানেল পাসওয়ার্ড / পিন</label>
                        <input 
                          type="password" 
                          defaultValue="••••••••" 
                          className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1" 
                        />
                      </div>
                    </div>
                  </div>

                  <div className="pt-2 flex justify-end">
                    <button 
                      onClick={async () => {
                        setStored("config", config);
                        await syncDocToCloud("config", "store", config);
                        notify("ব্র্যান্ড, লোগো ও থিম সেটিংস সফলভাবে ক্লাউডে সংরক্ষিত হয়েছে! 🎨👑");
                      }} 
                      className="bg-[#7a1c2b] text-white px-8 py-2.5 rounded-full text-xs font-bold shadow-md hover:bg-[#681727] transition active:scale-95 flex items-center gap-2"
                    >
                      <span>💾</span>
                      <span>ব্র্যান্ড সেটিংস সংরক্ষণ করুন (Save Brand Settings)</span>
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* ========================================================= */}
            {/* 11.2. THEME CUSTOMIZATION: ANNOUNCEMENT BAR & HEADER */}
            {/* ========================================================= */}
            {activeTab === "Header" && (
              <div className="bg-white p-6 sm:p-8 rounded-3xl border border-[#f3e2d0] space-y-6 shadow-sm animate-fadeIn">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-[#f3e2d0]">
                  <div>
                    <h3 className="text-xl sm:text-2xl font-bold text-[#7a1c2b] flex items-center gap-2">
                      <span>📢</span>
                      <span>নোটিশ বার ও হেডার ডিসপ্লে সেটিংস</span>
                    </h3>
                    <p className="text-xs text-gray-500 mt-0.5">
                      টপ অ্যানাউন্সমেন্ট বার এবং হেডার নেভিগেশন কম্পোনেন্টসমূহ নিয়ন্ত্রণ করুন
                    </p>
                  </div>
                  <button 
                    onClick={async () => {
                      setStored("config", config);
                      await syncDocToCloud("config", "store", config);
                      notify("হেডার ও নোটিশ সেটিংস ক্লাউডে সংরক্ষিত হয়েছে! ✅");
                    }} 
                    className="bg-[#7a1c2b] hover:bg-[#63141f] text-white px-6 py-2.5 rounded-full text-xs font-bold transition shadow flex items-center gap-1.5 active:scale-95"
                  >
                    <span>💾</span>
                    <span>সেটিংস সংরক্ষণ করুন</span>
                  </button>
                </div>

                <div className="space-y-5">
                  {/* Announcement Bar */}
                  <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                    <div className="flex items-center justify-between">
                      <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">টপ নোটিশ / অ্যানাউন্সমেন্ট বার</h4>
                      <label className="flex items-center gap-2 font-bold text-xs text-[#7a1c2b] cursor-pointer">
                        <input 
                          type="checkbox" 
                          checked={config.announcement.enabled} 
                          onChange={(e) => setConfig({ ...config, announcement: { ...config.announcement, enabled: e.target.checked } })} 
                          className="w-4 h-4 accent-[#7a1c2b]"
                        />
                        নোটিশ বার চালু রাখুন
                      </label>
                    </div>
                    <input 
                      type="text" 
                      placeholder="অ্যানাউন্সমেন্ট টেক্সট লিখুন..." 
                      value={config.announcement.text} 
                      onChange={(e) => setConfig({ ...config, announcement: { ...config.announcement, text: e.target.value } })}
                      className="w-full bg-white border rounded-full px-5 py-2.5 text-sm outline-none focus:border-[#7a1c2b]" 
                    />
                  </div>

                  {/* Header Toggles */}
                  <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                    <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">হেডার ডিসপ্লে টগলস</h4>
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-1">
                      <label className="flex items-center gap-2 text-xs bg-white p-3 rounded-2xl border cursor-pointer hover:border-[#7a1c2b] transition">
                        <input type="checkbox" checked={config?.header?.showSearch} onChange={(e) => setConfig({ ...config, header: { ...config.header, showSearch: e.target.checked } })} className="accent-[#7a1c2b]" />
                        সার্চ বক্স প্রদর্শন
                      </label>
                      <label className="flex items-center gap-2 text-xs bg-white p-3 rounded-2xl border cursor-pointer hover:border-[#7a1c2b] transition">
                        <input type="checkbox" checked={config.header.showCart} onChange={(e) => setConfig({ ...config, header: { ...config.header, showCart: e.target.checked } })} className="accent-[#7a1c2b]" />
                        কার্ট বাটন প্রদর্শন
                      </label>
                      <label className="flex items-center gap-2 text-xs bg-white p-3 rounded-2xl border cursor-pointer hover:border-[#7a1c2b] transition">
                        <input type="checkbox" checked={config.header.showCategories} onChange={(e) => setConfig({ ...config, header: { ...config.header, showCategories: e.target.checked } })} className="accent-[#7a1c2b]" />
                        ক্যাটাগরি বার
                      </label>
                      <label className="flex items-center gap-2 text-xs bg-white p-3 rounded-2xl border cursor-pointer hover:border-[#7a1c2b] transition">
                        <input type="checkbox" checked={config?.header?.sticky} onChange={(e) => setConfig({ ...config, header: { ...config.header, sticky: e.target.checked } })} className="accent-[#7a1c2b]" />
                        স্টিকি হেডার
                      </label>
                    </div>
                  </div>

                  <div className="pt-2 flex justify-end">
                    <button 
                      onClick={async () => {
                        setStored("config", config);
                        await syncDocToCloud("config", "store", config);
                        notify("হেডার সেটিংস সফলভাবে ক্লাউডে সংরক্ষিত হয়েছে! ✅");
                      }} 
                      className="bg-[#7a1c2b] text-white px-8 py-2.5 rounded-full text-xs font-bold shadow hover:bg-[#681727] transition active:scale-95 flex items-center gap-2"
                    >
                      <span>💾</span>
                      <span>হেডার সেটিংস সংরক্ষণ করুন (Save Header Settings)</span>
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* ========================================================= */}
            {/* 11.3. THEME CUSTOMIZATION: FOOTER, HELPLINE & SOCIAL */}
            {/* ========================================================= */}
            {activeTab === "Footer" && (
              <div className="bg-white p-6 sm:p-8 rounded-3xl border border-[#f3e2d0] space-y-6 shadow-sm animate-fadeIn">
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-[#f3e2d0]">
                  <div>
                    <h3 className="text-xl sm:text-2xl font-bold text-[#7a1c2b] flex items-center gap-2">
                      <span>📄</span>
                      <span>ফুটার, হেল্পলাইন ও সোশ্যাল সেটিংস</span>
                    </h3>
                    <p className="text-xs text-gray-500 mt-0.5">
                      স্টোর পরিচিতি, কন্টাক্ট হটলাইন, শোরুমের পূর্ণ ঠিকানা, সোশ্যাল মিডিয়া লিঙ্ক ও পেমেন্ট মেথড
                    </p>
                  </div>
                  <button 
                    onClick={async () => {
                      setStored("config", config);
                      await syncDocToCloud("config", "store", config);
                      notify("ফুটার ও যোগাযোগ সেটিংস ক্লাউডে সংরক্ষিত হয়েছে! ✅");
                    }} 
                    className="bg-[#7a1c2b] hover:bg-[#63141f] text-white px-6 py-2.5 rounded-full text-xs font-bold transition shadow flex items-center gap-1.5 active:scale-95"
                  >
                    <span>💾</span>
                    <span>সেটিংস সংরক্ষণ করুন</span>
                  </button>
                </div>

                <div className="space-y-5">
                  {/* Contact Info */}
                  <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                    <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">কন্টাক্ট ও হেল্পলাইন তথ্য</h4>
                    <div className="grid md:grid-cols-3 gap-3">
                      <div>
                        <label className="text-xs font-semibold text-gray-700">হটলাইন ফোন নম্বর</label>
                        <input 
                          type="text" 
                          value={config.phone} 
                          onChange={(e) => setConfig({ ...config, phone: e.target.value })}
                          className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1 focus:border-[#7a1c2b]" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">WhatsApp হেল্পলাইন নম্বর</label>
                        <input 
                          type="text" 
                          value={config.whatsapp} 
                          onChange={(e) => setConfig({ ...config, whatsapp: e.target.value })}
                          className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1 focus:border-[#7a1c2b]" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">ইমেইল ঠিকানা</label>
                        <input 
                          type="text" 
                          value={config.email} 
                          onChange={(e) => setConfig({ ...config, email: e.target.value })}
                          className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1 focus:border-[#7a1c2b]" 
                        />
                      </div>
                    </div>
                    <div>
                      <label className="text-xs font-semibold text-gray-700">দোকান / শোরুমের পূর্ণাঙ্গ ঠিকানা</label>
                      <input 
                        type="text" 
                        value={config.address} 
                        onChange={(e) => setConfig({ ...config, address: e.target.value })}
                        className="w-full bg-white border rounded-full px-4 py-2 text-xs outline-none mt-1 focus:border-[#7a1c2b]" 
                      />
                    </div>
                  </div>

                  {/* Footer About & Social Links */}
                  <div className="p-5 bg-[#fffaf5] rounded-3xl border border-[#f3e2d0] space-y-3">
                    <h4 className="font-bold text-sm text-[#7a1c2b] uppercase tracking-wider">ফুটার পরিচিতি ও সোশ্যাল মিডিয়া</h4>
                    <div>
                      <label className="text-xs font-semibold text-gray-700">ফুটার পরিচিতি (About Store)</label>
                      <textarea 
                        rows="2" 
                        value={config.footer.about} 
                        onChange={(e) => setConfig({ ...config, footer: { ...config.footer, about: e.target.value } })}
                        className="w-full bg-white border rounded-2xl p-3 text-xs outline-none mt-1 focus:border-[#7a1c2b]"
                      />
                    </div>
                    <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-1">
                      <div>
                        <label className="text-xs font-semibold text-gray-700">Facebook লিঙ্ক</label>
                        <input 
                          type="text" 
                          value={config.footer.fb} 
                          onChange={(e) => setConfig({ ...config, footer: { ...config.footer, fb: e.target.value } })}
                          className="w-full bg-white border rounded-full px-3 py-1.5 text-xs outline-none mt-1" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">Instagram লিঙ্ক</label>
                        <input 
                          type="text" 
                          value={config.footer.insta} 
                          onChange={(e) => setConfig({ ...config, footer: { ...config.footer, insta: e.target.value } })}
                          className="w-full bg-white border rounded-full px-3 py-1.5 text-xs outline-none mt-1" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">YouTube লিঙ্ক</label>
                        <input 
                          type="text" 
                          value={config.footer.youtube} 
                          onChange={(e) => setConfig({ ...config, footer: { ...config.footer, youtube: e.target.value } })}
                          className="w-full bg-white border rounded-full px-3 py-1.5 text-xs outline-none mt-1" 
                        />
                      </div>
                      <div>
                        <label className="text-xs font-semibold text-gray-700">TikTok লিঙ্ক</label>
                        <input 
                          type="text" 
                          value={config.footer.tiktok || ''} 
                          onChange={(e) => setConfig({ ...config, footer: { ...config.footer, tiktok: e.target.value } })}
                          className="w-full bg-white border rounded-full px-3 py-1.5 text-xs outline-none mt-1" 
                        />
                      </div>
                    </div>

                    <div className="pt-2">
                      <p className="text-xs font-bold text-[#7a1c2b] mb-2">ফুটার পেমেন্ট ব্যাজ টগল:</p>
                      <div className="flex flex-wrap gap-4 text-xs font-semibold">
                        <label className="flex items-center gap-1.5 cursor-pointer">
                          <input type="checkbox" checked={config.footer.bkash} onChange={(e) => setConfig({ ...config, footer: { ...config.footer, bkash: e.target.checked } })} className="accent-[#7a1c2b]" /> bKash
                        </label>
                        <label className="flex items-center gap-1.5 cursor-pointer">
                          <input type="checkbox" checked={config.footer.nagad} onChange={(e) => setConfig({ ...config, footer: { ...config.footer, nagad: e.target.checked } })} className="accent-[#7a1c2b]" /> Nagad
                        </label>
                        <label className="flex items-center gap-1.5 cursor-pointer">
                          <input type="checkbox" checked={config.footer.rocket} onChange={(e) => setConfig({ ...config, footer: { ...config.footer, rocket: e.target.checked } })} className="accent-[#7a1c2b]" /> Rocket
                        </label>
                        <label className="flex items-center gap-1.5 cursor-pointer">
                          <input type="checkbox" checked={config.footer.cod} onChange={(e) => setConfig({ ...config, footer: { ...config.footer, cod: e.target.checked } })} className="accent-[#7a1c2b]" /> Cash on Delivery
                        </label>
                      </div>
                    </div>
                  </div>

                  <div className="pt-2 flex justify-end">
                    <button 
                      onClick={async () => {
                        setStored("config", config);
                        await syncDocToCloud("config", "store", config);
                        notify("ফুটার ও কন্টাক্ট সেটিংস সফলভাবে ক্লাউডে সংরক্ষিত হয়েছে! ✅");
                      }} 
                      className="bg-[#7a1c2b] text-white px-8 py-2.5 rounded-full text-xs font-bold shadow hover:bg-[#681727] transition active:scale-95 flex items-center gap-2"
                    >
                      <span>💾</span>
                      <span>ফুটার সেটিংস সংরক্ষণ করুন (Save Footer Settings)</span>
                    </button>
                  </div>
                </div>
              </div>
            )}
"""
    content = content[:p_general.start()] + new_general_replacement + content[p_general.end():]

    # 6. Now eliminate the duplicate Admin & Header block after Backup tab
    p_admin_dup = re.search(r"\{\/\* =+\s*\*\/\}\s*\{\/\* ADMIN SETTINGS TAB - EXACT REPLICA OF THE SCREENSHOT \*\/\}[\s\S]*?(?=\{\/\* CART DRAWER \*\/\} )", content)
    assert p_admin_dup, "p_admin_dup not found"

    new_closing = """                </main>
              </div>
          )}

          """
    content = content[:p_admin_dup.start()] + new_closing + content[p_admin_dup.end():]

    with open("index.html", "w", encoding="utf-8") as f:
        f.write(content)

    with open("public/index.html", "w", encoding="utf-8") as f:
        f.write(content)

    print("SUCCESS: index.html and public/index.html refactored!")

if __name__ == "__main__":
    refactor()
