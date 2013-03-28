/**********************************************************************
 *
 *  Copyright Ian Quick 2002
 *  
 *  This file is part of MicroMpeg.
 *
 *   MicroMpeg is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   MicroMpeg is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MicroMpeg; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **********************************************************************/
package com.evilinc.mpeg.audio;

public abstract class FP
{
    private static final int PRECISION = 16;
    private static final int COS_TABLE_LENGTH = 4096;
    public  static final int PI = 205887;
    private static final int COS_TO_SIN = 768*4;
    private static final int COS_MASK = COS_TABLE_LENGTH - 1;
    private static final int COS_LEN_FP = toFP( COS_TABLE_LENGTH );
    private static final int COS_CONSTANT = div( COS_LEN_FP, mul(toFP(2), PI ) );

    public static int getPrecision()
    {
        return PRECISION;
    }

    public static final int getPI()
    {
        return PI;
    }

    public static final int inv(int num)
    {
        return FP.div( FP.toFP(1), num );
    }

    public static final int cos( int angle )
    {
        angle = toInt(mul(angle, COS_CONSTANT) );

        if(angle < 0 )
            angle = -angle;

        return COS_TABLE[ angle & COS_MASK ];
    }

    public static final int sin( int angle )
    {
        angle = toInt(mul(angle, COS_CONSTANT ) );

        return (  ( angle < 0 ) ?
                  -COS_TABLE[ (-angle + COS_TO_SIN) & COS_MASK ] :
                  COS_TABLE[ (angle + COS_TO_SIN ) & COS_MASK ]);
    }
            
    public static final int getAngle( int i, int j )
    {
        int k  = acos(j);

        return ( i < 0 ) ? ( (PI << 1) - k ) : k;
    }

    public static final int degToRad( int i )
    {
        return div( mul(i,PI), toFP(180) );
    }

    public static int radToDeg(int i)
    {
        return div( mul(i, toFP(180)), PI );
    }

    public static final int toInt(int i)
    {
        return (i + 32768) >> 16;
    }

    public static final int toFP(int i)
    {
        return i << 16;
    }

    public static final int mul( int i, int j)
    {
        return (int)( ( (long)i * (long)j )  >> 16 );
    }

    public static int add(int i, int j)
    {
        return i + j;
    }

    public static int sub(int i, int j)
    {
        return i - j;
    }

    public static int sqrt( int i )
    {
        throw new RuntimeException("Not Implemented");
    }

    public static final int div( int i, int j)
    {
        long l = (long) i;

        // Shifts by > 31 trap on Xpresso
        l <<= 16;
        l <<= 16;

        return (int) ( ( l / (long)j ) >> 16 );
    }

    public static int tan(int i)
    {
        return div( sin(i), cos(i) );
    }

    public static int cot(int i)
    {
        return div( FP.toFP(1), tan(i));
    }

    public static int acos(int i)
    {
        if(i > 256)
        {
            i = 256;
        }
        else if(i < -256)
        {
            i = -256;
        }
        int j = 0;
        int k = 512;
        int l = 0;
        int i1 = 0;
        while(j < k) 
        {
            l = j + k >> 1;
            if(l == i1)
                if(l == j)
                {
                    l++;
                    j++;
                } else
                    if(l == k)
                    {
                        l--;
                        k--;
                    }
            i1 = l;
            int j1 = COS_TABLE[l];
            if(j1 == i || j1 > i && COS_TABLE[l + 1] < i || j1 < i && COS_TABLE[l - 1] > i)
                return l;
            if(j1 < i)
                k = l;
            else
                j = l;
        }
        return l;
    }
    
    private static final int[] COS_TABLE =
    {
65536, 65536, 65536, 65535, 65535, 65534, 65533, 65532, 
65531, 65530, 65528, 65527, 65525, 65523, 65521, 65519, 
65516, 65514, 65511, 65508, 65505, 65502, 65499, 65495, 
65492, 65488, 65484, 65480, 65476, 65471, 65467, 65462, 
65457, 65452, 65447, 65442, 65436, 65430, 65425, 65419, 
65413, 65406, 65400, 65393, 65387, 65380, 65373, 65366, 
65358, 65351, 65343, 65336, 65328, 65320, 65311, 65303, 
65294, 65286, 65277, 65268, 65259, 65249, 65240, 65230, 
65220, 65210, 65200, 65190, 65180, 65169, 65159, 65148, 
65137, 65126, 65114, 65103, 65091, 65079, 65067, 65055, 
65043, 65031, 65018, 65006, 64993, 64980, 64967, 64953, 
64940, 64926, 64912, 64899, 64884, 64870, 64856, 64841, 
64827, 64812, 64797, 64782, 64766, 64751, 64735, 64720, 
64704, 64688, 64672, 64655, 64639, 64622, 64605, 64588, 
64571, 64554, 64536, 64519, 64501, 64483, 64465, 64447, 
64429, 64410, 64392, 64373, 64354, 64335, 64316, 64296, 
64277, 64257, 64237, 64217, 64197, 64177, 64156, 64136, 
64115, 64094, 64073, 64052, 64031, 64009, 63987, 63966, 
63944, 63922, 63899, 63877, 63854, 63832, 63809, 63786, 
63763, 63739, 63716, 63692, 63668, 63645, 63621, 63596, 
63572, 63547, 63523, 63498, 63473, 63448, 63423, 63397, 
63372, 63346, 63320, 63294, 63268, 63242, 63215, 63189, 
63162, 63135, 63108, 63081, 63054, 63026, 62998, 62971, 
62943, 62915, 62886, 62858, 62830, 62801, 62772, 62743, 
62714, 62685, 62655, 62626, 62596, 62566, 62536, 62506, 
62476, 62445, 62415, 62384, 62353, 62322, 62291, 62260, 
62228, 62197, 62165, 62133, 62101, 62069, 62036, 62004, 
61971, 61939, 61906, 61873, 61839, 61806, 61772, 61739, 
61705, 61671, 61637, 61603, 61568, 61534, 61499, 61464, 
61429, 61394, 61359, 61324, 61288, 61253, 61217, 61181, 
61145, 61108, 61072, 61035, 60999, 60962, 60925, 60888, 
60851, 60813, 60776, 60738, 60700, 60662, 60624, 60586, 
60547, 60509, 60470, 60431, 60392, 60353, 60314, 60275, 
60235, 60195, 60156, 60116, 60075, 60035, 59995, 59954, 
59914, 59873, 59832, 59791, 59750, 59708, 59667, 59625, 
59583, 59541, 59499, 59457, 59415, 59372, 59330, 59287, 
59244, 59201, 59158, 59114, 59071, 59027, 58983, 58940, 
58896, 58851, 58807, 58763, 58718, 58673, 58628, 58583, 
58538, 58493, 58448, 58402, 58356, 58311, 58265, 58219, 
58172, 58126, 58079, 58033, 57986, 57939, 57892, 57845, 
57798, 57750, 57703, 57655, 57607, 57559, 57511, 57463, 
57414, 57366, 57317, 57268, 57219, 57170, 57121, 57072, 
57022, 56972, 56923, 56873, 56823, 56773, 56722, 56672, 
56621, 56571, 56520, 56469, 56418, 56367, 56315, 56264, 
56212, 56160, 56108, 56056, 56004, 55952, 55900, 55847, 
55794, 55742, 55689, 55636, 55582, 55529, 55476, 55422, 
55368, 55314, 55260, 55206, 55152, 55098, 55043, 54989, 
54934, 54879, 54824, 54769, 54714, 54658, 54603, 54547, 
54491, 54435, 54379, 54323, 54267, 54210, 54154, 54097, 
54040, 53983, 53926, 53869, 53812, 53754, 53697, 53639, 
53581, 53523, 53465, 53407, 53349, 53290, 53232, 53173, 
53114, 53055, 52996, 52937, 52878, 52818, 52759, 52699, 
52639, 52579, 52519, 52459, 52398, 52338, 52277, 52217, 
52156, 52095, 52034, 51973, 51911, 51850, 51789, 51727, 
51665, 51603, 51541, 51479, 51417, 51354, 51292, 51229, 
51166, 51104, 51041, 50977, 50914, 50851, 50787, 50724, 
50660, 50596, 50532, 50468, 50404, 50340, 50275, 50211, 
50146, 50081, 50016, 49951, 49886, 49821, 49756, 49690, 
49624, 49559, 49493, 49427, 49361, 49295, 49228, 49162, 
49095, 49029, 48962, 48895, 48828, 48761, 48694, 48626, 
48559, 48491, 48424, 48356, 48288, 48220, 48152, 48084, 
48015, 47947, 47878, 47809, 47741, 47672, 47603, 47534, 
47464, 47395, 47325, 47256, 47186, 47116, 47046, 46976, 
46906, 46836, 46765, 46695, 46624, 46554, 46483, 46412, 
46341, 46270, 46199, 46127, 46056, 45984, 45912, 45841, 
45769, 45697, 45625, 45552, 45480, 45408, 45335, 45262, 
45190, 45117, 45044, 44971, 44898, 44824, 44751, 44677, 
44604, 44530, 44456, 44382, 44308, 44234, 44160, 44086, 
44011, 43937, 43862, 43787, 43713, 43638, 43563, 43487, 
43412, 43337, 43261, 43186, 43110, 43034, 42958, 42882, 
42806, 42730, 42654, 42578, 42501, 42424, 42348, 42271, 
42194, 42117, 42040, 41963, 41886, 41808, 41731, 41653, 
41576, 41498, 41420, 41342, 41264, 41186, 41108, 41029, 
40951, 40872, 40794, 40715, 40636, 40557, 40478, 40399, 
40320, 40241, 40161, 40082, 40002, 39922, 39843, 39763, 
39683, 39603, 39523, 39442, 39362, 39282, 39201, 39120, 
39040, 38959, 38878, 38797, 38716, 38635, 38554, 38472, 
38391, 38309, 38228, 38146, 38064, 37982, 37900, 37818, 
37736, 37654, 37572, 37489, 37407, 37324, 37241, 37159, 
37076, 36993, 36910, 36827, 36744, 36660, 36577, 36493, 
36410, 36326, 36243, 36159, 36075, 35991, 35907, 35823, 
35738, 35654, 35570, 35485, 35401, 35316, 35231, 35146, 
35062, 34977, 34892, 34806, 34721, 34636, 34551, 34465, 
34380, 34294, 34208, 34122, 34037, 33951, 33865, 33778, 
33692, 33606, 33520, 33433, 33347, 33260, 33173, 33087, 
33000, 32913, 32826, 32739, 32652, 32565, 32477, 32390, 
32303, 32215, 32127, 32040, 31952, 31864, 31776, 31688, 
31600, 31512, 31424, 31336, 31248, 31159, 31071, 30982, 
30893, 30805, 30716, 30627, 30538, 30449, 30360, 30271, 
30182, 30093, 30003, 29914, 29824, 29735, 29645, 29555, 
29466, 29376, 29286, 29196, 29106, 29016, 28926, 28835, 
28745, 28655, 28564, 28474, 28383, 28293, 28202, 28111, 
28020, 27929, 27838, 27747, 27656, 27565, 27474, 27382, 
27291, 27200, 27108, 27017, 26925, 26833, 26742, 26650, 
26558, 26466, 26374, 26282, 26190, 26098, 26005, 25913, 
25821, 25728, 25636, 25543, 25451, 25358, 25265, 25172, 
25080, 24987, 24894, 24801, 24708, 24614, 24521, 24428, 
24335, 24241, 24148, 24054, 23961, 23867, 23774, 23680, 
23586, 23492, 23398, 23304, 23210, 23116, 23022, 22928, 
22834, 22740, 22645, 22551, 22457, 22362, 22268, 22173, 
22078, 21984, 21889, 21794, 21699, 21604, 21510, 21415, 
21320, 21224, 21129, 21034, 20939, 20844, 20748, 20653, 
20557, 20462, 20366, 20271, 20175, 20080, 19984, 19888, 
19792, 19696, 19600, 19505, 19409, 19313, 19216, 19120, 
19024, 18928, 18832, 18735, 18639, 18543, 18446, 18350, 
18253, 18156, 18060, 17963, 17867, 17770, 17673, 17576, 
17479, 17382, 17285, 17188, 17091, 16994, 16897, 16800, 
16703, 16606, 16508, 16411, 16314, 16216, 16119, 16021, 
15924, 15826, 15729, 15631, 15534, 15436, 15338, 15240, 
15143, 15045, 14947, 14849, 14751, 14653, 14555, 14457, 
14359, 14261, 14163, 14065, 13966, 13868, 13770, 13672, 
13573, 13475, 13376, 13278, 13180, 13081, 12983, 12884, 
12785, 12687, 12588, 12490, 12391, 12292, 12193, 12095, 
11996, 11897, 11798, 11699, 11600, 11501, 11402, 11303, 
11204, 11105, 11006, 10907, 10808, 10709, 10609, 10510, 
10411, 10312, 10212, 10113, 10014, 9914, 9815, 9716, 
9616, 9517, 9417, 9318, 9218, 9119, 9019, 8919, 
8820, 8720, 8621, 8521, 8421, 8322, 8222, 8122, 
8022, 7923, 7823, 7723, 7623, 7523, 7423, 7323, 
7224, 7124, 7024, 6924, 6824, 6724, 6624, 6524, 
6424, 6324, 6224, 6123, 6023, 5923, 5823, 5723, 
5623, 5523, 5422, 5322, 5222, 5122, 5022, 4921, 
4821, 4721, 4621, 4520, 4420, 4320, 4219, 4119, 
4019, 3918, 3818, 3718, 3617, 3517, 3417, 3316, 
3216, 3115, 3015, 2914, 2814, 2714, 2613, 2513, 
2412, 2312, 2211, 2111, 2010, 1910, 1809, 1709, 
1608, 1508, 1407, 1307, 1206, 1106, 1005, 905, 
804, 704, 603, 503, 402, 302, 201, 101, 
0, -101, -201, -302, -402, -503, -603, -704, 
-804, -905, -1005, -1106, -1206, -1307, -1407, -1508, 
-1608, -1709, -1809, -1910, -2010, -2111, -2211, -2312, 
-2412, -2513, -2613, -2714, -2814, -2914, -3015, -3115, 
-3216, -3316, -3417, -3517, -3617, -3718, -3818, -3918, 
-4019, -4119, -4219, -4320, -4420, -4520, -4621, -4721, 
-4821, -4921, -5022, -5122, -5222, -5322, -5422, -5523, 
-5623, -5723, -5823, -5923, -6023, -6123, -6224, -6324, 
-6424, -6524, -6624, -6724, -6824, -6924, -7024, -7124, 
-7224, -7323, -7423, -7523, -7623, -7723, -7823, -7923, 
-8022, -8122, -8222, -8322, -8421, -8521, -8621, -8720, 
-8820, -8919, -9019, -9119, -9218, -9318, -9417, -9517, 
-9616, -9716, -9815, -9914, -10014, -10113, -10212, -10312, 
-10411, -10510, -10609, -10709, -10808, -10907, -11006, -11105, 
-11204, -11303, -11402, -11501, -11600, -11699, -11798, -11897, 
-11996, -12095, -12193, -12292, -12391, -12490, -12588, -12687, 
-12785, -12884, -12983, -13081, -13180, -13278, -13376, -13475, 
-13573, -13672, -13770, -13868, -13966, -14065, -14163, -14261, 
-14359, -14457, -14555, -14653, -14751, -14849, -14947, -15045, 
-15143, -15240, -15338, -15436, -15534, -15631, -15729, -15826, 
-15924, -16021, -16119, -16216, -16314, -16411, -16508, -16606, 
-16703, -16800, -16897, -16994, -17091, -17188, -17285, -17382, 
-17479, -17576, -17673, -17770, -17867, -17963, -18060, -18156, 
-18253, -18350, -18446, -18543, -18639, -18735, -18832, -18928, 
-19024, -19120, -19216, -19312, -19409, -19505, -19600, -19696, 
-19792, -19888, -19984, -20080, -20175, -20271, -20366, -20462, 
-20557, -20653, -20748, -20844, -20939, -21034, -21129, -21224, 
-21320, -21415, -21510, -21604, -21699, -21794, -21889, -21984, 
-22078, -22173, -22268, -22362, -22457, -22551, -22645, -22740, 
-22834, -22928, -23022, -23116, -23210, -23304, -23398, -23492, 
-23586, -23680, -23774, -23867, -23961, -24054, -24148, -24241, 
-24335, -24428, -24521, -24614, -24708, -24801, -24894, -24987, 
-25080, -25172, -25265, -25358, -25451, -25543, -25636, -25728, 
-25821, -25913, -26005, -26098, -26190, -26282, -26374, -26466, 
-26558, -26650, -26742, -26833, -26925, -27017, -27108, -27200, 
-27291, -27382, -27474, -27565, -27656, -27747, -27838, -27929, 
-28020, -28111, -28202, -28293, -28383, -28474, -28564, -28655, 
-28745, -28835, -28926, -29016, -29106, -29196, -29286, -29376, 
-29466, -29555, -29645, -29735, -29824, -29914, -30003, -30093, 
-30182, -30271, -30360, -30449, -30538, -30627, -30716, -30805, 
-30893, -30982, -31071, -31159, -31248, -31336, -31424, -31512, 
-31600, -31688, -31776, -31864, -31952, -32040, -32127, -32215, 
-32303, -32390, -32477, -32565, -32652, -32739, -32826, -32913, 
-33000, -33087, -33173, -33260, -33347, -33433, -33520, -33606, 
-33692, -33778, -33865, -33951, -34037, -34122, -34208, -34294, 
-34380, -34465, -34551, -34636, -34721, -34806, -34892, -34977, 
-35062, -35146, -35231, -35316, -35401, -35485, -35570, -35654, 
-35738, -35823, -35907, -35991, -36075, -36159, -36243, -36326, 
-36410, -36493, -36577, -36660, -36744, -36827, -36910, -36993, 
-37076, -37159, -37241, -37324, -37407, -37489, -37572, -37654, 
-37736, -37818, -37900, -37982, -38064, -38146, -38228, -38309, 
-38391, -38472, -38554, -38635, -38716, -38797, -38878, -38959, 
-39040, -39120, -39201, -39282, -39362, -39442, -39523, -39603, 
-39683, -39763, -39843, -39922, -40002, -40082, -40161, -40241, 
-40320, -40399, -40478, -40557, -40636, -40715, -40794, -40872, 
-40951, -41029, -41108, -41186, -41264, -41342, -41420, -41498, 
-41576, -41653, -41731, -41808, -41886, -41963, -42040, -42117, 
-42194, -42271, -42348, -42424, -42501, -42578, -42654, -42730, 
-42806, -42882, -42958, -43034, -43110, -43186, -43261, -43337, 
-43412, -43487, -43562, -43638, -43713, -43787, -43862, -43937, 
-44011, -44086, -44160, -44234, -44308, -44382, -44456, -44530, 
-44604, -44677, -44751, -44824, -44898, -44971, -45044, -45117, 
-45190, -45262, -45335, -45408, -45480, -45552, -45625, -45697, 
-45769, -45841, -45912, -45984, -46056, -46127, -46199, -46270, 
-46341, -46412, -46483, -46554, -46624, -46695, -46765, -46836, 
-46906, -46976, -47046, -47116, -47186, -47256, -47325, -47395, 
-47464, -47534, -47603, -47672, -47741, -47809, -47878, -47947, 
-48015, -48084, -48152, -48220, -48288, -48356, -48424, -48491, 
-48559, -48626, -48694, -48761, -48828, -48895, -48962, -49029, 
-49095, -49162, -49228, -49295, -49361, -49427, -49493, -49559, 
-49624, -49690, -49756, -49821, -49886, -49951, -50016, -50081, 
-50146, -50211, -50275, -50340, -50404, -50468, -50532, -50596, 
-50660, -50724, -50787, -50851, -50914, -50977, -51041, -51104, 
-51166, -51229, -51292, -51354, -51417, -51479, -51541, -51603, 
-51665, -51727, -51789, -51850, -51911, -51973, -52034, -52095, 
-52156, -52217, -52277, -52338, -52398, -52459, -52519, -52579, 
-52639, -52699, -52759, -52818, -52878, -52937, -52996, -53055, 
-53114, -53173, -53232, -53290, -53349, -53407, -53465, -53523, 
-53581, -53639, -53697, -53754, -53812, -53869, -53926, -53983, 
-54040, -54097, -54154, -54210, -54267, -54323, -54379, -54435, 
-54491, -54547, -54603, -54658, -54714, -54769, -54824, -54879, 
-54934, -54989, -55043, -55098, -55152, -55206, -55260, -55314, 
-55368, -55422, -55476, -55529, -55582, -55636, -55689, -55742, 
-55794, -55847, -55900, -55952, -56004, -56056, -56108, -56160, 
-56212, -56264, -56315, -56367, -56418, -56469, -56520, -56571, 
-56621, -56672, -56722, -56773, -56823, -56873, -56923, -56972, 
-57022, -57072, -57121, -57170, -57219, -57268, -57317, -57366, 
-57414, -57463, -57511, -57559, -57607, -57655, -57703, -57750, 
-57798, -57845, -57892, -57939, -57986, -58033, -58079, -58126, 
-58172, -58219, -58265, -58311, -58356, -58402, -58448, -58493, 
-58538, -58583, -58628, -58673, -58718, -58763, -58807, -58851, 
-58896, -58940, -58983, -59027, -59071, -59114, -59158, -59201, 
-59244, -59287, -59330, -59372, -59415, -59457, -59499, -59541, 
-59583, -59625, -59667, -59708, -59750, -59791, -59832, -59873, 
-59914, -59954, -59995, -60035, -60075, -60116, -60156, -60195, 
-60235, -60275, -60314, -60353, -60392, -60431, -60470, -60509, 
-60547, -60586, -60624, -60662, -60700, -60738, -60776, -60813, 
-60851, -60888, -60925, -60962, -60999, -61035, -61072, -61108, 
-61145, -61181, -61217, -61253, -61288, -61324, -61359, -61394, 
-61429, -61464, -61499, -61534, -61568, -61603, -61637, -61671, 
-61705, -61739, -61772, -61806, -61839, -61873, -61906, -61939, 
-61971, -62004, -62036, -62069, -62101, -62133, -62165, -62197, 
-62228, -62260, -62291, -62322, -62353, -62384, -62415, -62445, 
-62476, -62506, -62536, -62566, -62596, -62626, -62655, -62685, 
-62714, -62743, -62772, -62801, -62830, -62858, -62886, -62915, 
-62943, -62971, -62998, -63026, -63054, -63081, -63108, -63135, 
-63162, -63189, -63215, -63242, -63268, -63294, -63320, -63346, 
-63372, -63397, -63423, -63448, -63473, -63498, -63523, -63547, 
-63572, -63596, -63621, -63645, -63668, -63692, -63716, -63739, 
-63763, -63786, -63809, -63832, -63854, -63877, -63899, -63922, 
-63944, -63966, -63987, -64009, -64031, -64052, -64073, -64094, 
-64115, -64136, -64156, -64177, -64197, -64217, -64237, -64257, 
-64277, -64296, -64316, -64335, -64354, -64373, -64392, -64410, 
-64429, -64447, -64465, -64483, -64501, -64519, -64536, -64554, 
-64571, -64588, -64605, -64622, -64639, -64655, -64672, -64688, 
-64704, -64720, -64735, -64751, -64766, -64782, -64797, -64812, 
-64827, -64841, -64856, -64870, -64884, -64899, -64912, -64926, 
-64940, -64953, -64967, -64980, -64993, -65006, -65018, -65031, 
-65043, -65055, -65067, -65079, -65091, -65103, -65114, -65126, 
-65137, -65148, -65159, -65169, -65180, -65190, -65200, -65210, 
-65220, -65230, -65240, -65249, -65259, -65268, -65277, -65286, 
-65294, -65303, -65311, -65320, -65328, -65336, -65343, -65351, 
-65358, -65366, -65373, -65380, -65387, -65393, -65400, -65406, 
-65413, -65419, -65425, -65430, -65436, -65442, -65447, -65452, 
-65457, -65462, -65467, -65471, -65476, -65480, -65484, -65488, 
-65492, -65495, -65499, -65502, -65505, -65508, -65511, -65514, 
-65516, -65519, -65521, -65523, -65525, -65527, -65528, -65530, 
-65531, -65532, -65533, -65534, -65535, -65535, -65536, -65536, 
-65536, -65536, -65536, -65535, -65535, -65534, -65533, -65532, 
-65531, -65530, -65528, -65527, -65525, -65523, -65521, -65519, 
-65516, -65514, -65511, -65508, -65505, -65502, -65499, -65495, 
-65492, -65488, -65484, -65480, -65476, -65471, -65467, -65462, 
-65457, -65452, -65447, -65442, -65436, -65430, -65425, -65419, 
-65413, -65406, -65400, -65393, -65387, -65380, -65373, -65366, 
-65358, -65351, -65343, -65336, -65328, -65320, -65311, -65303, 
-65294, -65286, -65277, -65268, -65259, -65249, -65240, -65230, 
-65220, -65210, -65200, -65190, -65180, -65169, -65159, -65148, 
-65137, -65126, -65114, -65103, -65091, -65079, -65067, -65055, 
-65043, -65031, -65018, -65006, -64993, -64980, -64967, -64953, 
-64940, -64926, -64912, -64899, -64884, -64870, -64856, -64841, 
-64827, -64812, -64797, -64782, -64766, -64751, -64735, -64720, 
-64704, -64688, -64672, -64655, -64639, -64622, -64605, -64588, 
-64571, -64554, -64536, -64519, -64501, -64483, -64465, -64447, 
-64429, -64410, -64392, -64373, -64354, -64335, -64316, -64296, 
-64277, -64257, -64237, -64217, -64197, -64177, -64156, -64136, 
-64115, -64094, -64073, -64052, -64031, -64009, -63987, -63966, 
-63944, -63922, -63899, -63877, -63854, -63832, -63809, -63786, 
-63763, -63739, -63716, -63692, -63668, -63645, -63621, -63596, 
-63572, -63547, -63523, -63498, -63473, -63448, -63423, -63397, 
-63372, -63346, -63320, -63294, -63268, -63242, -63215, -63189, 
-63162, -63135, -63108, -63081, -63054, -63026, -62998, -62971, 
-62943, -62915, -62886, -62858, -62830, -62801, -62772, -62743, 
-62714, -62685, -62655, -62626, -62596, -62566, -62536, -62506, 
-62476, -62445, -62415, -62384, -62353, -62322, -62291, -62260, 
-62228, -62197, -62165, -62133, -62101, -62069, -62036, -62004, 
-61971, -61939, -61906, -61873, -61839, -61806, -61772, -61739, 
-61705, -61671, -61637, -61603, -61568, -61534, -61499, -61464, 
-61429, -61394, -61359, -61324, -61288, -61253, -61217, -61181, 
-61145, -61108, -61072, -61035, -60999, -60962, -60925, -60888, 
-60851, -60813, -60776, -60738, -60700, -60662, -60624, -60586, 
-60547, -60509, -60470, -60431, -60392, -60353, -60314, -60275, 
-60235, -60195, -60156, -60116, -60075, -60035, -59995, -59954, 
-59914, -59873, -59832, -59791, -59750, -59708, -59667, -59625, 
-59583, -59541, -59499, -59457, -59415, -59372, -59330, -59287, 
-59244, -59201, -59158, -59114, -59071, -59027, -58983, -58940, 
-58896, -58851, -58807, -58763, -58718, -58673, -58628, -58583, 
-58538, -58493, -58448, -58402, -58356, -58311, -58265, -58219, 
-58172, -58126, -58079, -58033, -57986, -57939, -57892, -57845, 
-57798, -57750, -57703, -57655, -57607, -57559, -57511, -57463, 
-57414, -57366, -57317, -57268, -57219, -57170, -57121, -57072, 
-57022, -56972, -56923, -56873, -56823, -56773, -56722, -56672, 
-56621, -56571, -56520, -56469, -56418, -56367, -56315, -56264, 
-56212, -56160, -56108, -56056, -56004, -55952, -55900, -55847, 
-55794, -55742, -55689, -55636, -55582, -55529, -55476, -55422, 
-55368, -55314, -55260, -55206, -55152, -55098, -55043, -54989, 
-54934, -54879, -54824, -54769, -54714, -54658, -54603, -54547, 
-54491, -54435, -54379, -54323, -54267, -54210, -54154, -54097, 
-54040, -53983, -53926, -53869, -53812, -53754, -53697, -53639, 
-53581, -53523, -53465, -53407, -53349, -53290, -53232, -53173, 
-53114, -53055, -52996, -52937, -52878, -52818, -52759, -52699, 
-52639, -52579, -52519, -52459, -52398, -52338, -52277, -52217, 
-52156, -52095, -52034, -51973, -51911, -51850, -51789, -51727, 
-51665, -51603, -51541, -51479, -51417, -51354, -51292, -51229, 
-51166, -51104, -51041, -50977, -50914, -50851, -50787, -50724, 
-50660, -50596, -50532, -50468, -50404, -50340, -50275, -50211, 
-50146, -50081, -50016, -49951, -49886, -49821, -49756, -49690, 
-49624, -49559, -49493, -49427, -49361, -49295, -49228, -49162, 
-49095, -49029, -48962, -48895, -48828, -48761, -48694, -48626, 
-48559, -48491, -48424, -48356, -48288, -48220, -48152, -48084, 
-48015, -47947, -47878, -47809, -47741, -47672, -47603, -47534, 
-47464, -47395, -47325, -47256, -47186, -47116, -47046, -46976, 
-46906, -46836, -46765, -46695, -46624, -46554, -46483, -46412, 
-46341, -46270, -46199, -46127, -46056, -45984, -45912, -45841, 
-45769, -45697, -45625, -45552, -45480, -45408, -45335, -45262, 
-45190, -45117, -45044, -44971, -44898, -44824, -44751, -44677, 
-44604, -44530, -44456, -44382, -44308, -44234, -44160, -44086, 
-44011, -43937, -43862, -43787, -43713, -43638, -43562, -43487, 
-43412, -43337, -43261, -43186, -43110, -43034, -42958, -42882, 
-42806, -42730, -42654, -42578, -42501, -42424, -42348, -42271, 
-42194, -42117, -42040, -41963, -41886, -41808, -41731, -41653, 
-41576, -41498, -41420, -41342, -41264, -41186, -41108, -41029, 
-40951, -40872, -40794, -40715, -40636, -40557, -40478, -40399, 
-40320, -40241, -40161, -40082, -40002, -39922, -39843, -39763, 
-39683, -39603, -39523, -39442, -39362, -39282, -39201, -39120, 
-39040, -38959, -38878, -38797, -38716, -38635, -38554, -38472, 
-38391, -38309, -38228, -38146, -38064, -37982, -37900, -37818, 
-37736, -37654, -37572, -37489, -37407, -37324, -37241, -37159, 
-37076, -36993, -36910, -36827, -36744, -36660, -36577, -36493, 
-36410, -36326, -36243, -36159, -36075, -35991, -35907, -35823, 
-35738, -35654, -35570, -35485, -35401, -35316, -35231, -35146, 
-35062, -34977, -34892, -34806, -34721, -34636, -34551, -34465, 
-34380, -34294, -34208, -34122, -34037, -33951, -33865, -33778, 
-33692, -33606, -33520, -33433, -33347, -33260, -33173, -33087, 
-33000, -32913, -32826, -32739, -32652, -32565, -32477, -32390, 
-32303, -32215, -32127, -32040, -31952, -31864, -31776, -31688, 
-31600, -31512, -31424, -31336, -31248, -31159, -31071, -30982, 
-30893, -30805, -30716, -30627, -30538, -30449, -30360, -30271, 
-30182, -30093, -30003, -29914, -29824, -29735, -29645, -29555, 
-29466, -29376, -29286, -29196, -29106, -29016, -28926, -28835, 
-28745, -28655, -28564, -28474, -28383, -28293, -28202, -28111, 
-28020, -27929, -27838, -27747, -27656, -27565, -27474, -27382, 
-27291, -27200, -27108, -27017, -26925, -26833, -26742, -26650, 
-26558, -26466, -26374, -26282, -26190, -26098, -26005, -25913, 
-25821, -25728, -25636, -25543, -25451, -25358, -25265, -25172, 
-25080, -24987, -24894, -24801, -24708, -24614, -24521, -24428, 
-24335, -24241, -24148, -24054, -23961, -23867, -23774, -23680, 
-23586, -23492, -23398, -23304, -23210, -23116, -23022, -22928, 
-22834, -22740, -22645, -22551, -22457, -22362, -22268, -22173, 
-22078, -21984, -21889, -21794, -21699, -21604, -21510, -21415, 
-21320, -21224, -21129, -21034, -20939, -20844, -20748, -20653, 
-20557, -20462, -20366, -20271, -20175, -20080, -19984, -19888, 
-19792, -19696, -19600, -19505, -19409, -19312, -19216, -19120, 
-19024, -18928, -18832, -18735, -18639, -18543, -18446, -18350, 
-18253, -18156, -18060, -17963, -17867, -17770, -17673, -17576, 
-17479, -17382, -17285, -17188, -17091, -16994, -16897, -16800, 
-16703, -16606, -16508, -16411, -16314, -16216, -16119, -16021, 
-15924, -15826, -15729, -15631, -15534, -15436, -15338, -15240, 
-15143, -15045, -14947, -14849, -14751, -14653, -14555, -14457, 
-14359, -14261, -14163, -14065, -13966, -13868, -13770, -13672, 
-13573, -13475, -13376, -13278, -13180, -13081, -12983, -12884, 
-12785, -12687, -12588, -12490, -12391, -12292, -12193, -12095, 
-11996, -11897, -11798, -11699, -11600, -11501, -11402, -11303, 
-11204, -11105, -11006, -10907, -10808, -10709, -10609, -10510, 
-10411, -10312, -10212, -10113, -10014, -9914, -9815, -9716, 
-9616, -9517, -9417, -9318, -9218, -9119, -9019, -8919, 
-8820, -8720, -8621, -8521, -8421, -8322, -8222, -8122, 
-8022, -7923, -7823, -7723, -7623, -7523, -7423, -7323, 
-7224, -7124, -7024, -6924, -6824, -6724, -6624, -6524, 
-6424, -6324, -6224, -6123, -6023, -5923, -5823, -5723, 
-5623, -5523, -5422, -5322, -5222, -5122, -5022, -4921, 
-4821, -4721, -4621, -4520, -4420, -4320, -4219, -4119, 
-4019, -3918, -3818, -3718, -3617, -3517, -3417, -3316, 
-3216, -3115, -3015, -2914, -2814, -2714, -2613, -2513, 
-2412, -2312, -2211, -2111, -2010, -1910, -1809, -1709, 
-1608, -1508, -1407, -1307, -1206, -1106, -1005, -905, 
-804, -704, -603, -503, -402, -302, -201, -101, 
0, 101, 201, 302, 402, 503, 603, 704, 
804, 905, 1005, 1106, 1206, 1307, 1407, 1508, 
1608, 1709, 1809, 1910, 2010, 2111, 2211, 2312, 
2412, 2513, 2613, 2714, 2814, 2914, 3015, 3115, 
3216, 3316, 3417, 3517, 3617, 3718, 3818, 3918, 
4019, 4119, 4219, 4320, 4420, 4520, 4621, 4721, 
4821, 4921, 5022, 5122, 5222, 5322, 5422, 5523, 
5623, 5723, 5823, 5923, 6023, 6123, 6224, 6324, 
6424, 6524, 6624, 6724, 6824, 6924, 7024, 7124, 
7224, 7323, 7423, 7523, 7623, 7723, 7823, 7923, 
8022, 8122, 8222, 8322, 8421, 8521, 8621, 8720, 
8820, 8919, 9019, 9119, 9218, 9318, 9417, 9517, 
9616, 9716, 9815, 9914, 10014, 10113, 10212, 10312, 
10411, 10510, 10609, 10709, 10808, 10907, 11006, 11105, 
11204, 11303, 11402, 11501, 11600, 11699, 11798, 11897, 
11996, 12095, 12193, 12292, 12391, 12490, 12588, 12687, 
12785, 12884, 12983, 13081, 13180, 13278, 13376, 13475, 
13573, 13672, 13770, 13868, 13966, 14065, 14163, 14261, 
14359, 14457, 14555, 14653, 14751, 14849, 14947, 15045, 
15143, 15240, 15338, 15436, 15534, 15631, 15729, 15826, 
15924, 16021, 16119, 16216, 16314, 16411, 16508, 16606, 
16703, 16800, 16897, 16994, 17091, 17188, 17285, 17382, 
17479, 17576, 17673, 17770, 17867, 17963, 18060, 18156, 
18253, 18350, 18446, 18543, 18639, 18735, 18832, 18928, 
19024, 19120, 19216, 19313, 19409, 19505, 19600, 19696, 
19792, 19888, 19984, 20080, 20175, 20271, 20366, 20462, 
20557, 20653, 20748, 20844, 20939, 21034, 21129, 21224, 
21320, 21415, 21510, 21604, 21699, 21794, 21889, 21984, 
22078, 22173, 22268, 22362, 22457, 22551, 22645, 22740, 
22834, 22928, 23022, 23116, 23210, 23304, 23398, 23492, 
23586, 23680, 23774, 23867, 23961, 24054, 24148, 24241, 
24335, 24428, 24521, 24614, 24708, 24801, 24894, 24987, 
25080, 25172, 25265, 25358, 25451, 25543, 25636, 25728, 
25821, 25913, 26005, 26098, 26190, 26282, 26374, 26466, 
26558, 26650, 26742, 26833, 26925, 27017, 27108, 27200, 
27291, 27382, 27474, 27565, 27656, 27747, 27838, 27929, 
28020, 28111, 28202, 28293, 28383, 28474, 28564, 28655, 
28745, 28835, 28926, 29016, 29106, 29196, 29286, 29376, 
29466, 29555, 29645, 29735, 29824, 29914, 30003, 30093, 
30182, 30271, 30360, 30449, 30538, 30627, 30716, 30805, 
30893, 30982, 31071, 31159, 31248, 31336, 31424, 31512, 
31600, 31688, 31776, 31864, 31952, 32040, 32127, 32215, 
32303, 32390, 32477, 32565, 32652, 32739, 32826, 32913, 
33000, 33087, 33173, 33260, 33347, 33433, 33520, 33606, 
33692, 33778, 33865, 33951, 34037, 34122, 34208, 34294, 
34380, 34465, 34551, 34636, 34721, 34806, 34892, 34977, 
35062, 35146, 35231, 35316, 35401, 35485, 35570, 35654, 
35738, 35823, 35907, 35991, 36075, 36159, 36243, 36326, 
36410, 36493, 36577, 36660, 36744, 36827, 36910, 36993, 
37076, 37159, 37241, 37324, 37407, 37489, 37572, 37654, 
37736, 37818, 37900, 37982, 38064, 38146, 38228, 38309, 
38391, 38472, 38554, 38635, 38716, 38797, 38878, 38959, 
39040, 39120, 39201, 39282, 39362, 39442, 39523, 39603, 
39683, 39763, 39843, 39922, 40002, 40082, 40161, 40241, 
40320, 40399, 40478, 40557, 40636, 40715, 40794, 40872, 
40951, 41029, 41108, 41186, 41264, 41342, 41420, 41498, 
41576, 41653, 41731, 41808, 41886, 41963, 42040, 42117, 
42194, 42271, 42348, 42424, 42501, 42578, 42654, 42730, 
42806, 42882, 42958, 43034, 43110, 43186, 43261, 43337, 
43412, 43487, 43563, 43638, 43713, 43787, 43862, 43937, 
44011, 44086, 44160, 44234, 44308, 44382, 44456, 44530, 
44604, 44677, 44751, 44824, 44898, 44971, 45044, 45117, 
45190, 45262, 45335, 45408, 45480, 45552, 45625, 45697, 
45769, 45841, 45912, 45984, 46056, 46127, 46199, 46270, 
46341, 46412, 46483, 46554, 46624, 46695, 46765, 46836, 
46906, 46976, 47046, 47116, 47186, 47256, 47325, 47395, 
47464, 47534, 47603, 47672, 47741, 47809, 47878, 47947, 
48015, 48084, 48152, 48220, 48288, 48356, 48424, 48491, 
48559, 48626, 48694, 48761, 48828, 48895, 48962, 49029, 
49095, 49162, 49228, 49295, 49361, 49427, 49493, 49559, 
49624, 49690, 49756, 49821, 49886, 49951, 50016, 50081, 
50146, 50211, 50275, 50340, 50404, 50468, 50532, 50596, 
50660, 50724, 50787, 50851, 50914, 50977, 51041, 51104, 
51166, 51229, 51292, 51354, 51417, 51479, 51541, 51603, 
51665, 51727, 51789, 51850, 51911, 51973, 52034, 52095, 
52156, 52217, 52277, 52338, 52398, 52459, 52519, 52579, 
52639, 52699, 52759, 52818, 52878, 52937, 52996, 53055, 
53114, 53173, 53232, 53290, 53349, 53407, 53465, 53523, 
53581, 53639, 53697, 53754, 53812, 53869, 53926, 53983, 
54040, 54097, 54154, 54210, 54267, 54323, 54379, 54435, 
54491, 54547, 54603, 54658, 54714, 54769, 54824, 54879, 
54934, 54989, 55043, 55098, 55152, 55206, 55260, 55314, 
55368, 55422, 55476, 55529, 55582, 55636, 55689, 55742, 
55794, 55847, 55900, 55952, 56004, 56056, 56108, 56160, 
56212, 56264, 56315, 56367, 56418, 56469, 56520, 56571, 
56621, 56672, 56722, 56773, 56823, 56873, 56923, 56972, 
57022, 57072, 57121, 57170, 57219, 57268, 57317, 57366, 
57414, 57463, 57511, 57559, 57607, 57655, 57703, 57750, 
57798, 57845, 57892, 57939, 57986, 58033, 58079, 58126, 
58172, 58219, 58265, 58311, 58356, 58402, 58448, 58493, 
58538, 58583, 58628, 58673, 58718, 58763, 58807, 58851, 
58896, 58940, 58983, 59027, 59071, 59114, 59158, 59201, 
59244, 59287, 59330, 59372, 59415, 59457, 59499, 59541, 
59583, 59625, 59667, 59708, 59750, 59791, 59832, 59873, 
59914, 59954, 59995, 60035, 60075, 60116, 60156, 60195, 
60235, 60275, 60314, 60353, 60392, 60431, 60470, 60509, 
60547, 60586, 60624, 60662, 60700, 60738, 60776, 60813, 
60851, 60888, 60925, 60962, 60999, 61035, 61072, 61108, 
61145, 61181, 61217, 61253, 61288, 61324, 61359, 61394, 
61429, 61464, 61499, 61534, 61568, 61603, 61637, 61671, 
61705, 61739, 61772, 61806, 61839, 61873, 61906, 61939, 
61971, 62004, 62036, 62069, 62101, 62133, 62165, 62197, 
62228, 62260, 62291, 62322, 62353, 62384, 62415, 62445, 
62476, 62506, 62536, 62566, 62596, 62626, 62655, 62685, 
62714, 62743, 62772, 62801, 62830, 62858, 62886, 62915, 
62943, 62971, 62998, 63026, 63054, 63081, 63108, 63135, 
63162, 63189, 63215, 63242, 63268, 63294, 63320, 63346, 
63372, 63397, 63423, 63448, 63473, 63498, 63523, 63547, 
63572, 63596, 63621, 63645, 63668, 63692, 63716, 63739, 
63763, 63786, 63809, 63832, 63854, 63877, 63899, 63922, 
63944, 63966, 63987, 64009, 64031, 64052, 64073, 64094, 
64115, 64136, 64156, 64177, 64197, 64217, 64237, 64257, 
64277, 64296, 64316, 64335, 64354, 64373, 64392, 64410, 
64429, 64447, 64465, 64483, 64501, 64519, 64536, 64554, 
64571, 64588, 64605, 64622, 64639, 64655, 64672, 64688, 
64704, 64720, 64735, 64751, 64766, 64782, 64797, 64812, 
64827, 64841, 64856, 64870, 64884, 64899, 64912, 64926, 
64940, 64953, 64967, 64980, 64993, 65006, 65018, 65031, 
65043, 65055, 65067, 65079, 65091, 65103, 65114, 65126, 
65137, 65148, 65159, 65169, 65180, 65190, 65200, 65210, 
65220, 65230, 65240, 65249, 65259, 65268, 65277, 65286, 
65294, 65303, 65311, 65320, 65328, 65336, 65343, 65351, 
65358, 65366, 65373, 65380, 65387, 65393, 65400, 65406, 
65413, 65419, 65425, 65430, 65436, 65442, 65447, 65452, 
65457, 65462, 65467, 65471, 65476, 65480, 65484, 65488, 
65492, 65495, 65499, 65502, 65505, 65508, 65511, 65514, 
65516, 65519, 65521, 65523, 65525, 65527, 65528, 65530, 
65531, 65532, 65533, 65534, 65535, 65535, 65536, 65536, 
    };
}  