function RelativeTimePipe(locale) {

    const rtf = new Intl.RelativeTimeFormat(locale, { numeric: "auto", style: "long" });

    this.transform = (isoDate) => {
        const timeDiff = new Date().getTime() - new Date(isoDate).getTime();

        const dayDiff = Math.round(timeDiff / (1000 * 3600 * 24));
        let diff = dayDiff;
        let unit = 'day';

        if (dayDiff > 90) {
            diff = Math.round(dayDiff / 30.4167);
            unit = 'month';
        }

        if (diff > 24) {
            diff = Math.round(dayDiff / 365);
            unit = 'year';
        }

        return rtf.format(-diff, unit);
    }
}

const pipe = new RelativeTimePipe(navigator.language);

rtfTransform = (target) => {
    if (!(target instanceof HTMLElement || target.dataset.date)) {
        return;
    }

    const isoDate = target.dataset.date;
    target.innerText = pipe.transform(isoDate);
}
