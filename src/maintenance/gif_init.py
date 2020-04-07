class GifFactory():
    def _is_existing(self, cursor, command, url):
        cursor.execute("SELECT COUNT(*) FROM gif WHERE command = ? and url = ?", (command, url))
        result = cursor.fetchone()[0]
        if result >= 1:
            return True
        else:
            return False

    def _insert_gif(self, cursor, command, url, nsfw=0):
        if not self._is_existing(cursor, command, url):
            cursor.execute("INSERT INTO gif (command, url, active, nsfw) VALUES(?, ?, 1, ?)", (command, url, nsfw))

    def update_db(self, db_connection):
        cursor = db_connection.cursor()

        # Original image lists from the Miki bot (miki.ai)

        print("Inserting bite gifs")
        bite_list = [
            "https://i.imgur.com/FlwJbPh.gif",
            "https://i.imgur.com/opsXoPC.gif",
            "https://i.imgur.com/OJBdmxz.gif",
            "https://i.imgur.com/ffkBusx.gif",
            "https://i.imgur.com/0d1y9zF.gif",
            "https://i.imgur.com/1EtOphf.gif",
            "https://i.imgur.com/kUEaRIu.gif",
            "https://i.imgur.com/Kt1h4UE.gif",
            "https://i.imgur.com/f4MAKp8.gif",
            "https://i.imgur.com/7a5q1b9.gif",
            "https://i.imgur.com/q7fMHaI.gif",
            "https://i.imgur.com/LgUknRu.gif",
            "https://i.imgur.com/rSYvWUg.gif",
            "https://i.imgur.com/dqsYXOL.gif",
            "https://i.imgur.com/2K66vgG.gif",
            "https://i.imgur.com/VKgFBJY.gif",
            "https://i.imgur.com/r9QOkEA.gif"
        ]
        for bite in bite_list:
            self._insert_gif(cursor, "bite", bite)

        print("Inserting cake gifs")
        cake_list = [
            "http://i.imgur.com/CYyrjRQ.gif",
            "http://i.imgur.com/3nWbcNT.gif",
            "http://i.imgur.com/AhOVdff.gif",
            "http://i.imgur.com/QRJ6xqB.gif",
            "http://i.imgur.com/Fuc4BX7.gif",
            "http://i.imgur.com/VQjMsms.gif",
            "http://i.imgur.com/ZwJJzQu.gif",
            "http://i.imgur.com/NupHmFh.gif",
            "http://i.imgur.com/5bnJJKq.gif",
            "http://i.imgur.com/sxpETqU.gif",
            "http://i.imgur.com/eIMcqa9.gif",
            "http://i.imgur.com/7Wx5liV.gif",
            "http://i.imgur.com/vvfl1dE.gif",
            "http://i.imgur.com/FRZhiZe.gif",
            "http://i.imgur.com/3gYwmi1.gif",
            "http://i.imgur.com/8JRLiZd.gif",
            "http://i.imgur.com/tZ4vFdG.gif",
            "http://i.imgur.com/QRA92zQ.gif",
            "http://i.imgur.com/fIYP5ns.gif",
            "http://i.imgur.com/QqxuS8Z.gif",
            "http://i.imgur.com/cSyuIzr.gif",
            "http://i.imgur.com/66v1YeA.gif",
            "http://i.imgur.com/utiWU12.gif",
            "http://i.imgur.com/qbm2gpc.gif",
            "http://i.imgur.com/RcvnwDB.gif",
            "http://i.imgur.com/RmCiKjE.gif",
            "http://i.imgur.com/KpXAL1a.gif",
            "http://i.imgur.com/HWYGovk.gif",
            "http://i.imgur.com/U1ODYh3.gif",
            "http://i.imgur.com/YuGMHo6.gif",
            "http://i.imgur.com/czupsk9.gif",
            "http://i.imgur.com/CCTZA51.gif",
            "http://i.imgur.com/68ihQjk.gif",
            "http://i.imgur.com/UQykz2g.gif",
            "http://i.imgur.com/v6FWLm8.gif",
            "http://i.imgur.com/GdmGcMA.gif",
            "http://i.imgur.com/oyh9W7X.gif",
            "http://i.imgur.com/LPRofzz.gif",
            "http://i.imgur.com/Y6w6CqT.gif",
            "http://i.imgur.com/oi5fVl9.gif",
            "http://i.imgur.com/DBAEo1L.gif",
            "http://i.imgur.com/QSLpOIR.gif"
        ]
        for cake in cake_list:
            self._insert_gif(cursor, "cake", cake)

        print("Inserting confused gifs")
        confused_list = [
            "http://i.imgur.com/RCotXAK.png",
            "http://i.imgur.com/yN5cwQq.jpg",
            "http://i.imgur.com/5TkmRWv.png",
            "http://i.imgur.com/QBFQzCQ.png",
            "http://i.imgur.com/KjSp1W4.png",
            "http://i.imgur.com/mX6D68m.jpg",
            "http://i.imgur.com/ogA5GeN.jpg",
            "http://i.imgur.com/eCHsHQZ.jpg",
            "http://i.imgur.com/r0u2dBx.jpg",
            "http://i.imgur.com/d8oMJUg.jpg",
            "http://i.imgur.com/dkV331J.jpg",
            "http://i.imgur.com/U9N4oGR.jpg",
            "http://i.imgur.com/GA0ZtvR.jpg",
            "http://i.imgur.com/NQ2e3Dq.gif",
            "http://i.imgur.com/5HTugJ6.jpg",
            "http://i.imgur.com/MJrBLij.png",
            "http://i.imgur.com/JgjCHPd.jpg",
            "http://i.imgur.com/KIDXXHw.gif",
            "http://i.imgur.com/Eu0Yyqq.jpg",
            "http://i.imgur.com/P5V369I.png",
            "http://i.imgur.com/DtVEGde.gif",
            "http://i.imgur.com/xxNH850.jpg",
            "http://i.imgur.com/gytATzW.jpg",
            "http://i.imgur.com/UrDJVC0.jpg",
            "http://i.imgur.com/3GkAnYo.png",
            "http://i.imgur.com/qTXPgyI.jpg",
            "http://i.imgur.com/GmIXuso.png",
            "http://i.imgur.com/UM8XpgR.gif",
            "http://i.imgur.com/GhoKM0u.gif",
            "http://i.imgur.com/ehskzgF.gif",
            "http://i.imgur.com/2biawgF.gif",
            "http://i.imgur.com/D2WXDbd.gif",
            "http://i.imgur.com/1ogeK3A.gif",
            "http://i.imgur.com/djNBrtj.jpg",
            "http://i.imgur.com/VyabzAv.jpg"
        ]
        for confused in confused_list:
            self._insert_gif(cursor, "confused", confused)

        print("Inserting cry gifs")
        cry_list = [
            "http://i.imgur.com/TTUBf2r.gif",
            "http://i.imgur.com/TP6dYGh.gif",
            "http://i.imgur.com/o66oQyX.png",
            "http://i.imgur.com/6AP78bD.png",
            "http://i.imgur.com/IvMvs2K.gif",
            "http://i.imgur.com/0kdQ38I.gif",
            "http://i.imgur.com/0kdQ38I.gif",
            "http://i.imgur.com/YHYLO4E.gif",
            "http://i.imgur.com/wXqxiDs.gif",
            "http://i.imgur.com/jzafqAh.gif",
            "http://i.imgur.com/2HPoWSf.gif",
            "http://i.imgur.com/W7prbbo.gif",
            "http://i.imgur.com/cKqKcG3.gif",
            "http://i.imgur.com/GKO0EQD.gif",
            "http://i.imgur.com/cu825ub.gif",
            "http://i.imgur.com/TP6dYGh.gif",
            "http://i.imgur.com/uZ2WXyL.gif",
            "http://i.imgur.com/DhkvnpB.gif",
            "http://i.imgur.com/LbpaJMG.gif",
            "http://i.imgur.com/V7iS3ZR.gif",
            "http://i.imgur.com/TLoHpfE.gif",
            "http://i.imgur.com/35tYOoB.gif",
            "http://i.imgur.com/Q6I2fiy.gif",
            "http://i.imgur.com/7Tw9dPP.gif",
            "http://i.imgur.com/aIiuJg8.gif",
            "http://i.imgur.com/0xIG1kG.gif",
            "http://i.imgur.com/nE0Tdp0.gif",
            "http://i.imgur.com/mvyAx5q.gif",
            "http://i.imgur.com/diq8LxU.gif",
            "http://i.imgur.com/Zv7au0h.gif",
            "http://i.imgur.com/sOyqImI.gif",
            "http://i.imgur.com/ZRbHJcb.gif",
            "http://i.imgur.com/kysvK28.gif",
            "http://i.imgur.com/6tGAJ75.gif",
            "http://i.imgur.com/5k6aD7Z.gif",
            "http://i.imgur.com/B29MytB.gif",
            "http://i.imgur.com/FQx8zRj.gif",
            "http://i.imgur.com/5vUBsz4.gif",
            "http://i.imgur.com/rBMTG5o.gif",
            "http://i.imgur.com/qfcReCj.gif",
            "http://i.imgur.com/CRdCCoH.gif",
            "http://i.imgur.com/FVt8Jqr.gif",
            "http://i.imgur.com/mjziZGI.gif",
            "http://i.imgur.com/DEgkwBe.gif",
            "http://i.imgur.com/hfRw1my.gif",
            "http://i.imgur.com/Sus5vcM.gif",
            "http://i.imgur.com/HLmnS6S.gif",
            "http://i.imgur.com/w9UjKVR.gif",
            "http://i.imgur.com/QZvnKHs.gif",
            "http://i.imgur.com/Mw49bFm.gif",
            "http://i.imgur.com/UVxws3C.gif",
            "http://i.imgur.com/ekhYSVB.gif",
            "http://i.imgur.com/VOMpsf6.gif",
            "http://i.imgur.com/ZFnoy0i.gif",
            "http://i.imgur.com/180yuVH.gif",
            "http://i.imgur.com/3zVAY49.gif",
            "http://i.imgur.com/AFDevRo.gif",
            "http://i.imgur.com/T23nHVO.gif",
            "http://i.imgur.com/qZWhIOw.gif",
            "http://i.imgur.com/Iy2VjHw.gif",
            "http://i.imgur.com/DbUYdpj.gif",
            "http://i.imgur.com/XqYZOiv.gif",
            "http://i.imgur.com/sYV2GBp.gif",
            "http://i.imgur.com/hxbNeGL.gif",
            "http://i.imgur.com/RXdJczP.gif",
            "http://i.imgur.com/JzmQgZq.gif",
            "http://i.imgur.com/NkLgdj8.gif",
            "http://i.imgur.com/kMzX2d4.gif",
            "http://i.imgur.com/WLNfW3d.gif",
            "http://i.imgur.com/Oxk8HUp.gif",
            "http://i.imgur.com/HTlRErM.gif",
            "http://i.imgur.com/KKgROec.gif",
            "http://i.imgur.com/W0WetV3.gif",
            "http://i.imgur.com/Ny9alj7.gif",
            "http://i.imgur.com/HNBYRZb.gif",
            "http://i.imgur.com/WOqFHee.gif",
            "http://i.imgur.com/rmlZXaP.gif",
            "http://i.imgur.com/mcVLAXi.gif",
            "http://i.imgur.com/SalWtcC.gif",
            "http://i.imgur.com/pkT7JFw.gif",
            "http://i.imgur.com/Tx15hPX.gif",
            "http://i.imgur.com/YANiZ2a.gif",
            "http://i.imgur.com/31WnXZ7.gif"
        ]
        for cry in cry_list:
            self._insert_gif(cursor, "cry", cry)

        print("Inserting cuddle gifs")
        cuddle_list = [
            "http://i.imgur.com/xWTyaKY.gif",
            "http://i.imgur.com/K4lYduH.gif",
            "http://i.imgur.com/8kLQ55E.gif",
            "http://i.imgur.com/kd0F5bV.gif",
            "http://i.imgur.com/zG60zPk.gif",
            "http://i.imgur.com/ct76LIg.gif",
            "http://i.imgur.com/guBWT22.gif",
            "http://i.imgur.com/Asnv32U.gif"
        ]
        for cuddle in cuddle_list:
            self._insert_gif(cursor, "cuddle", cuddle)

        print("Inserting glare gifs")
        glare_list = [
            "http://i.imgur.com/ba9Skjf.gif",
            "http://i.imgur.com/V6oBWDn.gif",
            "http://i.imgur.com/PWXcVQf.gif",
            "http://i.imgur.com/nOwOSjA.gif",
            "http://i.imgur.com/mG2Hm8s.gif",
            "http://i.imgur.com/iiJCWns.gif",
            "http://i.imgur.com/onUZvOi.gif",
            "http://i.imgur.com/cZwkHOB.gif",
            "http://i.imgur.com/uehetOS.gif",
            "http://i.imgur.com/MAZIl3c.gif",
            "http://i.imgur.com/C1u3GwL.gif",
            "http://i.imgur.com/E7Nnian.gif",
            "http://i.imgur.com/2RKfil2.gif",
            "http://i.imgur.com/jcSpVTS.gif",
            "http://i.imgur.com/r2X5YfC.gif",
            "http://i.imgur.com/qGQry9o.gif",
            "http://i.imgur.com/rRMUuQu.gif",
            "http://i.imgur.com/v47st6k.gif",
            "http://i.imgur.com/iiJCWns.gif",
            "http://i.imgur.com/v47st6k.gif",
            "http://i.imgur.com/VQpxVLE.gif",
            "http://i.imgur.com/uu8cTZO.gif",
            "http://i.imgur.com/i4l9F8R.gif",
            "http://i.imgur.com/BXE2bKM.gif",
            "http://i.imgur.com/PeVwwzy.gif",
            "http://i.imgur.com/lvADpDY.gif",
            "http://i.imgur.com/RovvrqD.gif",
            "http://i.imgur.com/K40NP62.gif",
            "http://i.imgur.com/mC3JYtl.gif",
            "http://i.imgur.com/xQMxKTT.gif",
            "http://i.imgur.com/2hWR6br.gif",
            "http://i.imgur.com/UmhwZSk.gif",
            "http://i.imgur.com/LIgO56g.gif",
            "http://i.imgur.com/hRz09iS.gif",
            "http://i.imgur.com/gBZJx5a.gif",
            "http://i.imgur.com/cq9KBP6.gif",
            "http://i.imgur.com/gIMc3iL.gif",
            "http://i.imgur.com/UIUGfOn.gif",
            "http://i.imgur.com/dNYBTp8.gif",
            "http://i.imgur.com/xgb3wk2.gif",
            "http://i.imgur.com/qzPYYsK.gif"
        ]
        for glare in glare_list:
            self._insert_gif(cursor, "glare", glare)

        print("Inserting high five gifs")
        highfive_list = [
            "http://i.imgur.com/LOoXzd9.gif",
            "http://i.imgur.com/Kwe6pAn.gif",
            "http://i.imgur.com/JeWzGGl.gif",
            "http://i.imgur.com/dqVx2oM.gif",
            "http://i.imgur.com/4n1K6kV.gif",
            "http://i.imgur.com/206dwM0.gif",
            "http://i.imgur.com/4ybFKuz.gif",
            "http://i.imgur.com/21e7SHD.gif",
            "http://i.imgur.com/LOCVVvL.gif",
            "http://i.imgur.com/h2KJJUA.gif",
            "http://i.imgur.com/ZUe3F3P.gif",
            "http://i.imgur.com/8xuO60E.gif",
            "http://i.imgur.com/4tMP3wu.gif",
            "http://i.imgur.com/F9odBEE.gif",
            "http://i.imgur.com/U742vH8.gif",
            "http://i.imgur.com/BSMMYrn.gif",
            "http://i.imgur.com/IuXs0ES.gif",
            "http://i.imgur.com/Wxl5was.gif",
            "http://i.imgur.com/TPhdaez.gif",
            "http://i.imgur.com/ebQWKZU.gif",
            "http://i.imgur.com/XYA8ET8.gif"
        ]
        for highfive in highfive_list:
            self._insert_gif(cursor, "highfive", highfive)

        print("Inserting hug gifs")
        hug_list = [
            "http://i.imgur.com/FvSnQs8.gif",
            "http://i.imgur.com/rXEq7oU.gif",
            "http://i.imgur.com/b6vVMQO.gif",
            "http://i.imgur.com/KJNTXm3.gif",
            "http://i.imgur.com/gn18SX8.gif",
            "http://i.imgur.com/SUdqF9w.gif",
            "http://i.imgur.com/7C36d39.gif",
            "http://i.imgur.com/ZOINyyw.gif",
            "http://i.imgur.com/Imxjcio.gif",
            "http://i.imgur.com/GNUeLdo.gif",
            "http://i.imgur.com/K52NZ36.gif",
            "http://i.imgur.com/683fWwC.gif",
            "http://i.imgur.com/0RgdLt4.gif",
            "http://i.imgur.com/jxPPkM8.gif",
            "http://i.imgur.com/oExwffx.gif",
            "http://i.imgur.com/pCZpL5h.gif",
            "http://i.imgur.com/GvQOwuy.gif",
            "http://i.imgur.com/cLHRyeB.gif",
            "http://i.imgur.com/FVbzx1A.gif",
            "http://i.imgur.com/gMLlFNC.gif",
            "http://i.imgur.com/FOdbhav.gif",
            "http://i.imgur.com/xWTyaKY.gif",
            "http://i.imgur.com/MrEMpE6.gif",
            "http://i.imgur.com/Y9sMTP4.gif",
            "https://i.imgur.com/0bkZa2R.gif",
            "https://i.imgur.com/2pWAtLl.gif",
            "https://i.imgur.com/3aYhBvm.gif",
            "https://i.imgur.com/3VxaPl9.gif",
            "https://i.imgur.com/5m2lSE6.gif",
            "https://i.imgur.com/8qNeRKm.gif",
            "https://i.imgur.com/9L5FwI8.gif",
            "https://i.imgur.com/9rQB0Kh.gif",
            "https://i.imgur.com/8127BtK.gif",
            "https://i.imgur.com/c1HAZqs.gif",
            "https://i.imgur.com/CEjwWtW.gif",
            "https://i.imgur.com/cr8FvDi.gif",
            "https://i.imgur.com/EMWgZpS.gif",
            "https://i.imgur.com/fSCbsJg.gif",
            "https://i.imgur.com/i3lYZye.gif",
            "https://i.imgur.com/IW5Wrf9.gif",
            "https://i.imgur.com/KgvQa15.gif",
            "https://i.imgur.com/mGs21wg.gif",
            "https://i.imgur.com/oapaLEO.gif",
            "https://i.imgur.com/OaYDYFt.gif",
            "https://i.imgur.com/OKLKhPx.gif",
            "https://i.imgur.com/pPRYv84.gif",
            "https://i.imgur.com/Q9BnV6n.gif",
            "https://i.imgur.com/SNfnbZK.gif",
            "https://i.imgur.com/t8HtLMA.gif",
            "https://i.imgur.com/T9A4DfG.gif",
            "https://i.imgur.com/TIT7Dmt.gif",
            "https://i.imgur.com/TkQgH8v.gif",
            "https://i.imgur.com/UN29yMD.gif",
            "https://i.imgur.com/uSDoSDP.gif",
            "https://i.imgur.com/uzS8ass.gif",
            "https://i.imgur.com/wzvOKOQ.gif",
            "https://i.imgur.com/y7prhj4.gif",
            "https://i.imgur.com/YSQ6fC4.gif",
            "https://i.imgur.com/yTjkKjB.gif",
            "https://i.imgur.com/YtWCHd0.gif",
            "https://i.imgur.com/z1b0tse.gif"
        ]
        for hug in hug_list:
            self._insert_gif(cursor, "hug", hug)

        print("Inserting kiss gifs")
        kiss_list = [
            "http://i.imgur.com/QIPaYW3.gif",
            "http://i.imgur.com/wx3WXZu.gif",
            "http://i.imgur.com/ZzIQwHP.gif",
            "http://i.imgur.com/z3TEGxp.gif",
            "http://i.imgur.com/kJEr7Vu.gif",
            "http://i.imgur.com/IsIR4V0.gif",
            "http://i.imgur.com/bmeCqLM.gif",
            "http://i.imgur.com/LBWIJpu.gif",
            "http://i.imgur.com/p6hNamc.gif",
            "http://i.imgur.com/PPw83Ug.gif",
            "http://i.imgur.com/lZ7gAES.gif",
            "http://i.imgur.com/Bftud8V.gif",
            "http://i.imgur.com/AicG7H6.gif",
            "http://i.imgur.com/ql3FvuU.gif",
            "http://i.imgur.com/XLjH6zQ.gif",
            "http://i.imgur.com/W7arBvy.gif",
            "http://i.imgur.com/W9htMol.gif",
            "http://i.imgur.com/IVOBC8p.gif",
            "https://i.imgur.com/0WWWvat.gif",
            "https://i.imgur.com/4h7uBat.gif",
            "https://i.imgur.com/709chb9.gif",
            "https://i.imgur.com/CtXHoOd.gif",
            "https://i.imgur.com/cWnyExp.gif",
            "https://i.imgur.com/hcqmSPq.gif",
            "https://i.imgur.com/JGRB1pF.gif",
            "https://i.imgur.com/jjxalGq.gif",
            "https://i.imgur.com/KKP079r.gif",
            "https://i.imgur.com/KWM6eIB.gif",
            "https://i.imgur.com/MGdlYsj.gif",
            "https://i.imgur.com/mNEHfJ0.gif",
            "https://i.imgur.com/NPKIfOf.gif",
            "https://i.imgur.com/OE7lSSY.gif",
            "https://i.imgur.com/q8VV95J.gif",
            "https://i.imgur.com/RZ6myag.gif",
            "https://i.imgur.com/s3DggdT.gif",
            "https://i.imgur.com/YkrEkbF.gif",
            "https://i.imgur.com/YOQgZqY.gif",
            "https://i.imgur.com/YVkdbjp.gif",
            "https://i.imgur.com/zGFB0wJ.gif"

        ]
        for kiss in kiss_list:
            self._insert_gif(cursor, "kiss", kiss)

        print("Inserting lewd gifs")
        lewd_list = [
            "http://i.imgur.com/eG42EVs.png",
            "http://i.imgur.com/8shK3jh.png",
            "http://i.imgur.com/uLKC84x.jpg",
            "http://i.imgur.com/PZCwyyE.png",
            "http://i.imgur.com/KWklw30.png",
            "http://i.imgur.com/aoLsNgx.jpg",
            "http://i.imgur.com/wyJAMVt.jpg",
            "http://i.imgur.com/2Y5ZgHH.png",
            "http://i.imgur.com/OIZyqxL.jpg",
            "http://i.imgur.com/cejd1c0.gif",
            "http://i.imgur.com/Obl7JvE.png",
            "http://i.imgur.com/PFFmM1q.png",
            "http://i.imgur.com/2vopeCM.jpg",
            "http://i.imgur.com/U4Nk0e5.jpg",
            "http://i.imgur.com/Llf61b1.jpg",
            "http://i.imgur.com/3vYPbuO.jpg",
            "http://i.imgur.com/p1twVD4.png",
            "http://i.imgur.com/kRaopT0.gif",
            "http://i.imgur.com/On8Axls.gif",
            "http://i.imgur.com/yCqJlFc.gif",
            "http://i.imgur.com/jlTqATG.gif"
        ]
        for lewd in lewd_list:
            self._insert_gif(cursor, "lewd", lewd)

        print("Inserting lick gifs")
        lick_list = [
            "https://cdn.miki.ai/images/5112e59f-798d-4085-b947-a44dc03f6517.gif",
            "https://cdn.miki.ai/images/2187d457-703a-43a0-93de-3304774f972f.gif",
            "https://cdn.miki.ai/images/165eb268-7772-47d9-9e06-4f8c4b6fb1d0.gif",
            "https://cdn.miki.ai/images/82b45d73-4b6a-4886-b79f-d43282193843.gif",
            "https://cdn.miki.ai/images/c397769d-7563-43e1-a21e-e89498371bff.gif",
            "https://cdn.miki.ai/images/f56b349d-507d-46df-849a-584b718c345c.gif",
            "https://cdn.miki.ai/images/f38d5382-120b-4b26-9269-e1218eed4308.gif"
        ]
        for lick in lick_list:
            self._insert_gif(cursor, "lick", lick)

        print("Insert pat gifs")
        pat_list = [
            "http://i.imgur.com/Y2DrXtT.gif",
            "http://i.imgur.com/G7b4OnS.gif",
            "http://i.imgur.com/nQqH0Xa.gif",
            "http://i.imgur.com/mCtyWEr.gif",
            "http://i.imgur.com/Cju6UX3.gif",
            "http://i.imgur.com/0YkOcUC.gif",
            "http://i.imgur.com/QxZjpbV.gif",
            "http://i.imgur.com/0FLNsZX.gif",
            "http://i.imgur.com/nsiyoRQ.gif",
            "http://i.imgur.com/kWDrnc3.gif",
            "http://i.imgur.com/5c0JGlx.gif",
            "http://i.imgur.com/SuU9WQV.gif",
            "http://i.imgur.com/UuYqD7v.gif",
            "http://i.imgur.com/7wZ6s5M.gif",
            "http://i.imgur.com/VuucXay.gif",
            "http://i.imgur.com/pnb1k5P.gif",
            "http://i.imgur.com/cDKGlTX.gif",
            "http://i.imgur.com/JjWLlcz.gif",
            "http://i.imgur.com/4SiEFQq.gif",
            "http://i.imgur.com/JfRGrgw.gif",
            "http://i.imgur.com/HiKI49x.gif",
            "http://i.imgur.com/VBCPpjk.gif",
            "http://i.imgur.com/qL5SShC.gif",
            "http://i.imgur.com/fvgSWgw.gif",
            "http://i.imgur.com/bOrLVXd.gif",
            "http://i.imgur.com/UwcwNiU.gif",
            "http://i.imgur.com/Y9iZrGG.gif",
            "http://i.imgur.com/75FpUOd.gif",
            "http://i.imgur.com/V2VFPSj.gif",
            "http://i.imgur.com/RFd1Gar.gif",
            "http://i.imgur.com/bgXEKqK.gif",
            "http://i.imgur.com/rMeGX0k.gif",
            "http://i.imgur.com/SpoJHzQ.gif",
            "http://i.imgur.com/ZCucIDe.gif",
            "http://i.imgur.com/b2dC2pu.gif",
            "http://i.imgur.com/0SBqpld.gif",
            "http://i.imgur.com/FAHxGpn.gif",
            "http://i.imgur.com/Q8i2yZz.gif",
            "http://i.imgur.com/46QOOlu.gif",
            "http://i.imgur.com/XhuyMe4.gif",
            "http://i.imgur.com/1d9y1s1.gif",
            "http://i.imgur.com/npxQPMH.gif",
            "http://i.imgur.com/VcvVbSb.gif",
            "http://i.imgur.com/G7WpBeD.gif",
            "http://i.imgur.com/VMQhPNA.gif",
            "http://i.imgur.com/xbqhigm.gif",
            "http://i.imgur.com/ilc8zXi.gif",
            "http://i.imgur.com/4GgbYst.gif",
            "http://i.imgur.com/1mr4NWL.gif",
            "http://i.imgur.com/wXw7IjY.gif",
            "https://i.imgur.com/3wFMOxX.gif",
            "https://i.imgur.com/6blw2Lj.gif",
            "https://i.imgur.com/e0I4N2g.gif",
            "https://i.imgur.com/kutIuyK.gif",
            "https://i.imgur.com/lZst12K.gif"
        ]
        for pat in pat_list:
            self._insert_gif(cursor, "pat", pat)

        print("Inserting poke gifs")
        poke_list = [
            "http://i.imgur.com/WG8EKwM.gif",
            "http://i.imgur.com/dfoxby7.gif",
            "http://i.imgur.com/TzD1Ngz.gif",
            "http://i.imgur.com/i1hwvQu.gif",
            "http://i.imgur.com/bStOFsM.gif",
            "http://i.imgur.com/1PBeB9H.gif",
            "http://i.imgur.com/3kerpju.gif",
            "http://i.imgur.com/uMBRFjX.gif",
            "http://i.imgur.com/YDJFoBV.gif",
            "http://i.imgur.com/urC9B1H.gif"
        ]
        for poke in poke_list:
            self._insert_gif(cursor, "poke", poke)

        print("Inserting pout gifs")
        pout_list = [
            "http://i.imgur.com/hsjBcz1.jpg",
            "http://i.imgur.com/oJSVNzT.jpg",
            "http://i.imgur.com/gWtmHoN.jpg",
            "http://i.imgur.com/VFG9zKV.png",
            "http://i.imgur.com/BUBiL0f.jpg",
            "http://i.imgur.com/UdlZ69E.gif",
            "http://i.imgur.com/yhryAf9.png",
            "http://i.imgur.com/d9DG2sJ.png",
            "http://i.imgur.com/uTB2HIY.png",
            "http://i.imgur.com/dVtR9vI.png",
            "http://i.imgur.com/rt7Vgn3.jpg",
            "http://i.imgur.com/uTB2HIY.png"
        ]
        for pout in pout_list:
            self._insert_gif(cursor, "pout", pout)

        print("Inserting punch gifs")
        punch_list = [
            "http://imgur.com/jVc3GGv.gif",
            "http://imgur.com/iekwz4h.gif",
            "http://imgur.com/AbRmlAo.gif",
            "http://imgur.com/o5MoMYi.gif",
            "http://imgur.com/yNfMX9B.gif",
            "http://imgur.com/bwXvfKE.gif",
            "http://imgur.com/6wKJVHy.gif",
            "http://imgur.com/kokCK1I.gif",
            "http://imgur.com/E3CtvPV.gif",
            "http://imgur.com/q7AmR8n.gif",
            "http://imgur.com/pDohPrm.gif"
        ]
        for punch in punch_list:
            self._insert_gif(cursor, "punch", punch)

        print("Inserting slap gifs")
        slap_list = [
            "http://i.imgur.com/GQtzDsV.gif",
            "http://i.imgur.com/rk8eqnt.gif",
            "http://i.imgur.com/UnzGS24.gif",
            "http://i.imgur.com/CHbRGnV.gif",
            "http://i.imgur.com/DvwnC0r.gif",
            "http://i.imgur.com/Ksy8dvd.gif",
            "http://i.imgur.com/b75B4qM.gif",
            "http://i.imgur.com/d9thUdx.gif",
            "http://i.imgur.com/iekwz4h.gif",
            "http://i.imgur.com/q7AmR8n.gif",
            "http://i.imgur.com/pDohPrm.gif"
        ]
        for slap in slap_list:
            self._insert_gif(cursor, "slap", slap)

        print("Inserting smug gifs")
        smug_list = [
            "http://i.imgur.com/zUwqrhM.png",
            "http://i.imgur.com/TYqPh89.jpg",
            "http://i.imgur.com/xyOSaCt.png",
            "http://i.imgur.com/gyw0ifl.png",
            "http://i.imgur.com/kk0xvtx.png",
            "http://i.imgur.com/UIuyUne.jpg",
            "http://i.imgur.com/9zgIjY1.jpg",
            "http://i.imgur.com/Ku1ONAD.jpg",
            "http://i.imgur.com/7lB5bRT.jpg",
            "http://i.imgur.com/BoVHipF.jpg",
            "http://i.imgur.com/vN48mwz.png",
            "http://i.imgur.com/fGI4zLe.jpg",
            "http://i.imgur.com/Gc4gmwQ.jpg",
            "http://i.imgur.com/JMrmKt7.jpg",
            "http://i.imgur.com/a7sbJz2.jpg",
            "http://i.imgur.com/NebmjhR.png",
            "http://i.imgur.com/5ccbrFI.png",
            "http://i.imgur.com/XJL4Vmo.jpg",
            "http://i.imgur.com/eg0q1ez.png",
            "http://i.imgur.com/JJFxxmA.jpg",
            "http://i.imgur.com/2cTDF3b.jpg",
            "http://i.imgur.com/Xc0Duqv.png",
            "http://i.imgur.com/YgMdPkd.jpg",
            "http://i.imgur.com/BvAv6an.jpg",
            "http://i.imgur.com/KRLP5JT.jpg",
            "http://i.imgur.com/yXcsCK3.jpg",
            "http://i.imgur.com/QXG56kG.png",
            "http://i.imgur.com/OFBz1YJ.png",
            "http://i.imgur.com/9ulVckY.png",
            "http://i.imgur.com/VLXeSJK.png",
            "http://i.imgur.com/baiMBP6.png"
        ]
        for smug in smug_list:
            self._insert_gif(cursor, "smug", smug)

        print("Inserting stare gifs")
        stare_list = [
            "http://i.imgur.com/Bp9vfcf.gif",
            "http://i.imgur.com/7nFd5ve.gif",
            "http://i.imgur.com/rmfWuM0.gif"
        ]
        for stare in stare_list:
            self._insert_gif(cursor, "stare", stare)
