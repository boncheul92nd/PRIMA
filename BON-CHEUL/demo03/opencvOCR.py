# -*- coding: utf-8 -*-
import numpy as np
import cv2
import os
import sys
from PIL import Image
import pytesseract
import pymongo
import base64
from bson.binary import Binary

def edge_detect(image, fullPath):
    orig = image.copy()
    r = 800.0 / image.shape[0]
    dim = (int(image.shape[1] * r), 800)
    image = cv2.resize(image, dim, interpolation=cv2.INTER_AREA)

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (3, 3), 0)
    edged = cv2.Canny(gray, 75, 200)

    cv2.imwrite(fullPath+'Edged.png', edged)
    return edged, image, orig, r

def find_contour(edged, image, fullPath):
    (cnts, _) = cv2.findContours(edged.copy(), cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
    cnts = sorted(cnts, key=cv2.contourArea, reverse=True)[:5]

    for c in cnts:
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.02 * peri, True)

        if len(approx) == 4:
            screenCnt = approx
            break
    cv2.drawContours(image, [screenCnt], -1, (0, 255, 0), 2)
    cv2.imwrite(fullPath+'Outline.png', image)
    return screenCnt


def order_points(pts):
    rect = np.zeros((4, 2), dtype='float32')
    s = pts.sum(axis=1)
    rect[0] = pts[np.argmin(s)]
    rect[2] = pts[np.argmax(s)]
    diff = np.diff(pts, axis=1)
    rect[1] = pts[np.argmin(diff)]
    rect[3] = pts[np.argmax(diff)]
    return rect


def perspective_transform(orig, r, screenCnt, fullPath):
    rect = order_points(screenCnt.reshape(4, 2) / r)
    (topLeft, topRight, bottomRight, bottomLeft) = rect
    w1 = abs(bottomRight[0] - bottomLeft[0])
    w2 = abs(topRight[0] - topLeft[0])
    h1 = abs(topRight[1] - bottomRight[1])
    h2 = abs(topLeft[1] - bottomLeft[1])
    maxWidth = max([w1, w2])
    maxHeight = max([h1, h2])
    dst = np.float32([[0, 0], [maxWidth - 1, 0], [maxWidth - 1, maxHeight - 1], [0, maxHeight - 1]])
    M = cv2.getPerspectiveTransform(rect, dst)
    warped = cv2.warpPerspective(orig, M, (maxWidth, maxHeight))
    cv2.imwrite(fullPath+'Warped.png', warped)
    return warped

def adaptive_threshold(orig, warped, fullPath):
    warped = cv2.cvtColor(warped, cv2.COLOR_BGR2GRAY)
    warped = cv2.adaptiveThreshold(warped, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 21, 10)
    cv2.imwrite(fullPath+'Scanned.png', warped)

def ocr_tesseract(fullPath):
    image_file = fullPath+'Scanned.png'
    im = Image.open(image_file)
    tesseract_path = 'C:\\Program Files (x86)\\Tesseract-OCR\\tesseract'
    pytesseract.pytesseract.tesseract_cmd = tesseract_path

    textKor = pytesseract.image_to_string(im, lang='kor')
    textEng = pytesseract.image_to_string(im)

    f = open(fullPath+'Result.txt', 'w')
    f.write(textKor)
    f.close()

    print textKor
    print textEng

def auto_scan_image(path):
   fullPath = './uploads'+path
   list = os.listdir('./uploads'+path)
   for i in list:
        noExt = i[:8]
        if noExt == 'Original':
            filename = i
            break
   image = cv2.imread(fullPath+filename)
   edged, image, orig, r = edge_detect(image, fullPath)
   screenCnt = find_contour(edged, image, fullPath)
   warped = perspective_transform(orig, r, screenCnt, fullPath)
   adaptive_threshold(orig, warped, fullPath)
   ocr_tesseract(fullPath)

if __name__ == '__main__':
    path = sys.argv[1]
    auto_scan_image(path)

    Edged = open('./uploads'+path+'Edged.png', 'rb').read()
    Outline = open('./uploads' + path + 'Outline.png', 'rb').read()
    Warped = open('./uploads' + path + 'Warped.png', 'rb').read()
    Scanned = open('./uploads' + path + 'Scanned.png', 'rb').read()
    Result = open('./uploads' + path + 'Result.txt', 'rb').read()

    connection = pymongo.MongoClient("localhost", 27017)
    db = connection.PRIMA_DB
    collection = db.records

    collection.update({"date":path[1:len(path)-1]}, {
        "$set": {
            "img_edged": {
                "data":Binary(Edged),
                "contentType":"image/png"
            },
            "img_outline": {
                "data": Binary(Outline),
                "contentType": "image/png"
            },
            "img_warped": {
                "data": Binary(Warped),
                "contentType": "image/png"
            },
            "img_scanned": {
                "data": Binary(Scanned),
                "contentType": "image/png"
            },
            "number":Result
        }
    });